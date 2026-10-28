package com.mastermartini.app.ui.screens.perfil

import android.content.ActivityNotFoundException
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mastermartini.app.ui.components.*
import com.mastermartini.app.ui.viewmodels.PerfilViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

// Pantalla de perfil adaptada al boceto móvil moderno
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    volver: () -> Unit,
    intereses: () -> Unit,
    cerrarSesion: () -> Unit,
    vm: PerfilViewModel = viewModel()
) {
    val estado by vm.estado.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var pendiente by rememberSaveable { mutableStateOf("") }
    var confirmarSalida by rememberSaveable { mutableStateOf(false) }

    // Selector para abrir la galería del dispositivo
    val galeria = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) vm.guardarFoto(uri)
    }

    // Launcher para tomar fotos con la cámara
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

    // Carga la foto guardada en segundo plano
    val imagen by produceState<androidx.compose.ui.graphics.ImageBitmap?>(null, estado.foto) {
        value = withContext(Dispatchers.IO) {
            if (estado.foto.isBlank()) null else runCatching {
                BitmapFactory.decodeFile(estado.foto)?.asImageBitmap()
            }.getOrNull()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mi perfil",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1D1B20)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = volver) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color(0xFF1D1B20)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFCF9FC))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFCF9FC))
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!estado.listo) {
                CircularProgressIndicator()
            }

            // 1. Marco para la Foto de perfil con Badge de Cámara
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(Color(0xFFC1ED99)), // Verde pastel suave como en el boceto
                    contentAlignment = Alignment.Center
                ) {
                    if (imagen != null) {
                        Image(
                            bitmap = imagen!!,
                            contentDescription = "Foto de perfil",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color(0xFF386A20),
                            modifier = Modifier.size(64.dp)
                        )
                    }
                }

                // Insignia circular morada con ícono de cámara
                Box(
                    modifier = Modifier
                        .offset(x = 6.dp, y = 6.dp)
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF6750A4)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = "Cambiar foto",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Título y subtítulo de la sección de foto
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Foto de perfil",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1D1B20)
                )
                Text(
                    text = "Toma una foto o elige una imagen.",
                    fontSize = 14.sp,
                    color = Color(0xFF49454F)
                )
            }

            // Botones de acción: Cámara y Galería
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    enabled = estado.listo && !estado.cargando,
                    onClick = {
                        try {
                            val carpeta = File(context.cacheDir, "camara").apply { mkdirs() }
                            carpeta.listFiles()?.filter { it.name != pendiente }?.forEach { it.delete() }
                            val archivo = File.createTempFile("captura_", ".jpg", carpeta)
                            pendiente = archivo.name
                            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", archivo)
                            camara.launch(uri)
                        } catch (_: Exception) { vm.errorFoto() }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Icon(Icons.Default.PhotoCamera, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Cámara")
                }

                OutlinedButton(
                    enabled = estado.listo && !estado.cargando,
                    onClick = {
                        try { galeria.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
                        catch (_: ActivityNotFoundException) { vm.errorFoto() }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Icon(Icons.Default.PhotoLibrary, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Galería")
                }
            }

            // 2. Campos de Texto de Información del Usuario
            AppTextField(
                valor = estado.nombre,
                cambiar = vm::nombre,
                etiqueta = "Nombre",
                error = estado.errorNombre,
                icono = Icons.Default.Person,
                habilitado = !estado.cargando && estado.listo
            )

            AppTextField(
                valor = estado.correo,
                cambiar = {},
                etiqueta = "Correo electrónico",
                icono = Icons.Default.Email,
                soloLectura = true
            )

            AppTextField(
                valor = estado.telefono,
                cambiar = vm::telefono,
                etiqueta = "Teléfono",
                error = estado.errorTelefono,
                icono = Icons.Default.Phone,
                tipo = KeyboardType.Phone,
                habilitado = !estado.cargando && estado.listo
            )

            MensajeError(estado.mensaje)

            AnimatedVisibility(estado.aviso != null) {
                Text(estado.aviso.orEmpty(), color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
            }

            // Botón Guardar cambios
            AppButton(
                texto = "Guardar cambios",
                cargando = estado.cargando,
                habilitado = estado.listo,
                accion = vm::guardar
            )

            // 3. Tarjeta de Intereses
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F2FA)),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE8DEF8)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.RestaurantMenu,
                                contentDescription = null,
                                tint = Color(0xFF6750A4),
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "Intereses",
                                fontSize = 13.sp,
                                color = Color(0xFF49454F)
                            )
                            Text(
                                text = estado.intereses.joinToString().ifBlank { "Gastronomía" },
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1D1B20)
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = intereses,
                        enabled = !estado.cargando && estado.listo,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF6750A4))
                    ) {
                        Text("Cambiar intereses", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Botón para cerrar sesión
            TextButton(
                onClick = { confirmarSalida = true },
                enabled = !estado.cargando
            ) {
                Text("Cerrar sesión", color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }

    // Diálogo de confirmación para cerrar sesión
    if (confirmarSalida) {
        AlertDialog(
            onDismissRequest = { confirmarSalida = false },
            title = { Text("Cerrar sesión") },
            text = { Text("Tus datos y favoritos seguirán guardados en este dispositivo.") },
            confirmButton = { TextButton(onClick = cerrarSesion) { Text("Cerrar sesión", color = MaterialTheme.colorScheme.error) } },
            dismissButton = { TextButton(onClick = { confirmarSalida = false }) { Text("Cancelar") } }
        )
    }
}
