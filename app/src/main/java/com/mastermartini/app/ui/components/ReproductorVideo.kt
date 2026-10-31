package com.mastermartini.app.ui.components

import android.net.Uri
import android.os.Build
import android.media.MediaPlayer
import android.widget.VideoView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.delay

// Videos locales con controles propios. No necesitan conexión ni permisos de red.
@Composable
fun ReproductorVideo(videoResId: Int) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val uri = remember(videoResId) {
        Uri.parse("android.resource://${context.packageName}/$videoResId")
    }
    var posicion by rememberSaveable(videoResId) { mutableIntStateOf(0) }
    var reproducirAlPreparar by rememberSaveable(videoResId) { mutableStateOf(false) }
    var pantallaCompleta by rememberSaveable(videoResId) { mutableStateOf(false) }
    var duracion by remember(videoResId) { mutableIntStateOf(0) }
    var reproduciendo by remember(videoResId) { mutableStateOf(false) }
    var cargando by remember(videoResId) { mutableStateOf(true) }
    var error by remember(videoResId) { mutableStateOf(false) }
    var arrastrando by remember { mutableStateOf(false) }
    var vista by remember { mutableStateOf<VideoView?>(null) }

    // Pausar también al bloquear el celular o cambiar a otra aplicación.
    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, evento ->
            if (evento == Lifecycle.Event.ON_PAUSE) {
                vista?.let {
                    if (!error && !cargando) posicion = it.currentPosition.coerceAtLeast(0)
                    it.pause()
                }
                reproducirAlPreparar = false
                reproduciendo = false
            }
        }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(vista) {
        val actual = vista ?: return@LaunchedEffect
        while (true) {
            if (!error && !cargando && duracion > 0) {
                reproduciendo = actual.isPlaying
                if (!arrastrando) posicion = actual.currentPosition.coerceIn(0, duracion)
            }
            delay(300)
        }
    }

    fun cambiarPantalla() {
        vista?.let {
            if (!error && !cargando) posicion = it.currentPosition.coerceAtLeast(0)
            reproducirAlPreparar = it.isPlaying
            it.pause()
        }
        cargando = true
        duracion = 0
        pantallaCompleta = !pantallaCompleta
    }

    val reproductor: @Composable () -> Unit = {
        Column(if (pantallaCompleta) Modifier.fillMaxSize() else Modifier.fillMaxWidth()) {
            Box(
                Modifier.fillMaxWidth()
                    .then(if (pantallaCompleta) Modifier.weight(1f) else Modifier.aspectRatio(16f / 9f))
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                AndroidView(
                    factory = { ctx ->
                        VideoView(ctx).apply {
                            vista = this
                            setOnPreparedListener { media ->
                                duracion = media.duration.coerceAtLeast(1)
                                error = false
                                // Espera a que termine la búsqueda para conservar la posición al ampliar.
                                media.setOnSeekCompleteListener {
                                    cargando = false
                                    if (reproducirAlPreparar) start()
                                }
                                val destino = posicion.coerceIn(1, duracion)
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    media.seekTo(destino.toLong(), MediaPlayer.SEEK_CLOSEST)
                                } else {
                                    seekTo(destino)
                                }
                            }
                            setOnCompletionListener {
                                posicion = duracion
                                reproduciendo = false
                                reproducirAlPreparar = false
                            }
                            setOnErrorListener { _, _, _ ->
                                error = true
                                cargando = false
                                reproduciendo = false
                                reproducirAlPreparar = false
                                true
                            }
                            setVideoURI(uri)
                        }
                    },
                    onReset = null,
                    onRelease = { liberada ->
                        // Libera el sonido y el decodificador al salir de la vista.
                        liberada.stopPlayback()
                        if (vista === liberada) vista = null
                    },
                    modifier = Modifier.fillMaxSize()
                )
                if (cargando) CircularProgressIndicator(color = Color.White)
                if (error) Text("No se pudo abrir el video", color = Color.White)
            }
            Column(
                Modifier.fillMaxWidth().background(Color(0xFFF3EDF7)).padding(12.dp)
            ) {
                if (error) {
                    Text("Vuelve a intentarlo o regresa al catálogo.", color = Color(0xFF49454F))
                    TextButton(onClick = {
                        error = false
                        cargando = true
                        duracion = 0
                        posicion = 0
                        vista?.setVideoURI(uri)
                    }) { Text("Reintentar") }
                } else {
                    Slider(
                        value = posicion.toFloat().coerceIn(0f, duracion.coerceAtLeast(1).toFloat()),
                        onValueChange = { arrastrando = true; posicion = it.toInt() },
                        onValueChangeFinished = { vista?.seekTo(posicion); arrastrando = false },
                        valueRange = 0f..duracion.coerceAtLeast(1).toFloat(),
                        enabled = duracion > 0,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF6750A4), activeTrackColor = Color(0xFF6750A4)
                        )
                    )
                }
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    TextButton(
                        enabled = !error && duracion > 0,
                        colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF6750A4)),
                        onClick = {
                            vista?.let {
                                if (it.isPlaying) {
                                    it.pause()
                                    reproducirAlPreparar = false
                                    reproduciendo = false
                                } else {
                                    if (posicion >= duracion - 300) { posicion = 0; it.seekTo(0) }
                                    it.start()
                                    reproducirAlPreparar = true
                                    reproduciendo = true
                                }
                            }
                        }
                    ) {
                        Icon(if (reproduciendo) Icons.Default.Pause else Icons.Default.PlayArrow, null)
                        Text(if (reproduciendo) "Pausar" else "Reproducir")
                    }
                    Text("${tiempoVideo(posicion)} / ${tiempoVideo(duracion)}",
                        style = MaterialTheme.typography.labelMedium, color = Color(0xFF49454F))
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = ::cambiarPantalla) {
                        Icon(
                            if (pantallaCompleta) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                            contentDescription = if (pantallaCompleta) "Salir de pantalla completa" else "Pantalla completa",
                            tint = Color(0xFF6750A4)
                        )
                    }
                }
            }
        }
    }

    if (pantallaCompleta) {
        Dialog(
            onDismissRequest = ::cambiarPantalla,
            properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)
        ) {
            Box(
                Modifier.fillMaxSize().background(Color.Black).safeDrawingPadding(),
                contentAlignment = Alignment.Center
            ) { reproductor() }
        }
    } else {
        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp)) { reproductor() }
    }
}

private fun tiempoVideo(milisegundos: Int): String {
    val segundos = milisegundos / 1000
    return "%d:%02d".format(segundos / 60, segundos % 60)
}
