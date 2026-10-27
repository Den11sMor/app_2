package com.mastermartini.app.ui.screens.perfil

import android.content.ActivityNotFoundException
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mastermartini.app.ui.components.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun PerfilScreen(volver: () -> Unit, intereses: () -> Unit, cerrarSesion: () -> Unit,
                 vm: PerfilViewModel = viewModel()) {
    val estado by vm.estado.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var pendiente by rememberSaveable { mutableStateOf("") }
    var confirmarSalida by rememberSaveable { mutableStateOf(false) }
    val galeria = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) vm.guardarFoto(uri)
    }
    val camara = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { correcto ->
        if (pendiente.isNotBlank()) {
            val archivo = File(context.cacheDir, "camara/$pendiente")
            if (correcto) {
                val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", archivo)
                vm.guardarFoto(uri)
            } else archivo.delete()
            pendiente = ""
        }
    }
    val imagen by produceState<androidx.compose.ui.graphics.ImageBitmap?>(null, estado.foto) {
        value = withContext(Dispatchers.IO) {
            if (estado.foto.isBlank()) null else runCatching {
                BitmapFactory.decodeFile(estado.foto)?.asImageBitmap()
            }.getOrNull()
        }
    }
    AppPage("Mi perfil", volver) {
        if (!estado.listo) CircularProgressIndicator()
        imagen?.let {
            Image(it, "Foto de perfil", Modifier.fillMaxWidth().height(180.dp), contentScale = ContentScale.Fit)
        }
        Text("Foto de perfil", style = MaterialTheme.typography.titleMedium)
        Text("Toma una foto o elige una imagen. Solo se guardará la que selecciones.",
            style = MaterialTheme.typography.bodySmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(enabled = estado.listo && !estado.cargando, onClick = {
                try {
                    val carpeta = File(context.cacheDir, "camara").apply { mkdirs() }
                    // Solo se conserva la captura pendiente de esta sesión de cámara.
                    carpeta.listFiles()?.filter { it.name != pendiente }?.forEach { it.delete() }
                    val archivo = File.createTempFile("captura_", ".jpg", carpeta)
                    pendiente = archivo.name
                    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", archivo)
                    camara.launch(uri)
                } catch (_: Exception) { vm.errorFoto() }
            }) { Icon(Icons.Default.PhotoCamera, null); Spacer(Modifier.width(6.dp)); Text("Cámara") }
            OutlinedButton(enabled = estado.listo && !estado.cargando, onClick = {
                try { galeria.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
                catch (_: ActivityNotFoundException) { vm.errorFoto() }
            }) { Icon(Icons.Default.PhotoLibrary, null); Spacer(Modifier.width(6.dp)); Text("Galería") }
        }
        AppTextField(estado.nombre, vm::nombre, "Nombre", estado.errorNombre,
            Icons.Default.Person, habilitado = !estado.cargando && estado.listo)
        AppTextField(estado.correo, {}, "Correo electrónico", icono = Icons.Default.Email, soloLectura = true)
        AppTextField(estado.telefono, vm::telefono, "Teléfono", estado.errorTelefono,
            Icons.Default.Phone, KeyboardType.Phone, habilitado = !estado.cargando && estado.listo)
        MensajeError(estado.mensaje)
        AnimatedVisibility(estado.aviso != null) {
            Text(estado.aviso.orEmpty(), color = MaterialTheme.colorScheme.primary)
        }
        AppButton("Guardar cambios", estado.cargando, estado.listo, vm::guardar)
        Text("Intereses: ${estado.intereses.joinToString().ifBlank { "Sin seleccionar" }}")
        OutlinedButton(onClick = intereses, enabled = !estado.cargando && estado.listo) { Text("Cambiar intereses") }
        TextButton(onClick = { confirmarSalida = true }, enabled = !estado.cargando) { Text("Cerrar sesión") }
    }
    if (confirmarSalida) AlertDialog(
        onDismissRequest = { confirmarSalida = false },
        title = { Text("Cerrar sesión") },
        text = { Text("Tus datos y favoritos seguirán guardados en este dispositivo.") },
        confirmButton = { TextButton(onClick = cerrarSesion) { Text("Cerrar sesión") } },
        dismissButton = { TextButton(onClick = { confirmarSalida = false }) { Text("Cancelar") } }
    )
}
