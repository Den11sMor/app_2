package com.mastermartini.app.ui.screens.detalle

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
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mastermartini.app.ui.components.*

// Pantalla de Detalle de Receta/Contenido adaptada al boceto estándar con soporte para fotos
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleContenidoScreen(id: Int, volver: () -> Unit, vm: DetalleViewModel = viewModel()) {
    val estado by vm.estado.collectAsStateWithLifecycle()
    val segundos by vm.segundos.collectAsStateWithLifecycle()
    val terminado by vm.terminado.collectAsStateWithLifecycle()
    val contenido = estado.contenidos.find { it.id == id }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = contenido?.tipo ?: "Receta",
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            if (estado.cargando) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF6750A4))
                }
            }

            MensajeError(estado.mensaje)

            if (!estado.cargando && contenido == null) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("No se encontró el contenido.", fontSize = 16.sp, color = Color(0xFF49454F))
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = vm::cargar) { Text("Volver a intentar") }
                }
            }

            contenido?.let { item ->
                val imagenRes = item.obtenerImagenResId(context)
                val videoRes = item.obtenerVideoResId(context)

                // 1. Reproductor de Video (Si es un contenido de tipo Video y existe el archivo en res/raw/)
                if (item.tipo == "Video" && videoRes != null) {
                    ReproductorVideoView(videoRes)
                } else {
                    // Ilustración / Foto de Portada con bordes suavemente curvados
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(210.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color(0xFFE8DEF8)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (imagenRes != null) {
                            Image(
                                painter = painterResource(imagenRes),
                                contentDescription = item.titulo,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = when (item.tipo) {
                                    "Receta" -> Icons.Default.RestaurantMenu
                                    "Video" -> Icons.Default.PlayCircle
                                    else -> Icons.Default.School
                                },
                                contentDescription = item.titulo,
                                tint = Color(0xFF6750A4),
                                modifier = Modifier.size(90.dp)
                            )
                        }
                    }
                }

                // 2. Título, Categoría y Descripción
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = item.titulo,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1D1B20)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestaurantMenu,
                            contentDescription = null,
                            tint = Color(0xFF6750A4),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "${item.categoria} · ${item.duracion}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF49454F)
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = item.descripcion,
                        fontSize = 15.sp,
                        color = Color(0xFF49454F),
                        lineHeight = 20.sp
                    )
                }

                // 3. Procesamiento del detalle (Separación inteligente de Ingredientes, Preparación o Unidades de Curso)
                val partes = item.detalle.split("\n\nPreparación\n", "\n\nPrograma de estudio\n", "\n\nGuía de estudio\n")
                val ingredientesTexto = partes.getOrNull(0)?.removePrefix("Ingredientes\n").orEmpty()
                val preparacionTexto = partes.getOrNull(1).orEmpty()

                // Si es un CURSO, mostramos la Ficha del Curso con Ficha de Datos, Programa de Unidades, Progreso y Certificado
                if (item.tipo == "Curso") {
                    // Ficha de Datos Destacados del Curso
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3EDF7)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                text = "Información del curso",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1D1B20)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Icon(Icons.Default.School, null, tint = Color(0xFF6750A4), modifier = Modifier.size(18.dp))
                                        Text("Nivel: ${item.categoria}", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1D1B20))
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Icon(Icons.Default.Timer, null, tint = Color(0xFF6750A4), modifier = Modifier.size(18.dp))
                                        Text("Duración: ${item.duracion}", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1D1B20))
                                    }
                                }

                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF6750A4), modifier = Modifier.size(18.dp))
                                        Text("Modalidad: Online", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1D1B20))
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Icon(Icons.Default.WorkspacePremium, null, tint = Color(0xFF6750A4), modifier = Modifier.size(18.dp))
                                        Text("Certificado incluido", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1D1B20))
                                    }
                                }
                            }
                        }
                    }

                    // Programa de Estudio con Unidades Formateadas
                    if (preparacionTexto.isNotBlank() || item.detalle.contains("Unidad")) {
                        val unidades = item.detalle.lines().filter { it.contains("Unidad") || it.contains("Actividad") }
                        
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFE8DEF8)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.School,
                                        contentDescription = null,
                                        tint = Color(0xFF6750A4),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Text(
                                    text = "Programa de Unidades",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1D1B20)
                                )
                            }

                            unidades.forEachIndexed { index, unidad ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(18.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F2FA))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFFE8DEF8)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "${index + 1}",
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF6750A4)
                                            )
                                        }

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = unidad,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF1D1B20)
                                            )
                                            Text(
                                                text = "Lección teórico-práctica con evaluación guiada",
                                                fontSize = 12.sp,
                                                color = Color(0xFF49454F)
                                            )
                                        }

                                        Icon(
                                            imageVector = Icons.Default.PlayCircle,
                                            contentDescription = "Iniciar unidad",
                                            tint = Color(0xFF6750A4),
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Proyecto y Certificación
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF7FC)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE7E0EC))
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Icon(Icons.Default.Star, null, tint = Color(0xFF6750A4))
                                Text("Proyecto y Certificado", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D1B20))
                            }
                            Text(
                                text = "Al completar las unidades de este curso, podrás descargar tu Certificado de Participación Master Martini y registrar tu proyecto en tu perfil.",
                                fontSize = 14.sp,
                                color = Color(0xFF49454F),
                                lineHeight = 19.sp
                            )
                        }
                    }
                }

                // Sección 3A: Card de Ingredientes (Para Recetas)
                if (item.tipo != "Curso" && ingredientesTexto.isNotBlank()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F2FA)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFE8DEF8)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SoupKitchen,
                                        contentDescription = null,
                                        tint = Color(0xFF6750A4),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Text(
                                    text = "Ingredientes",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1D1B20)
                                )
                            }

                            // Lista de ingredientes formateada
                            val listaIngredientes = ingredientesTexto.lines().filter { it.isNotBlank() }
                            listaIngredientes.forEach { ingrediente ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF6750A4))
                                    )
                                    Text(
                                        text = ingrediente,
                                        fontSize = 15.sp,
                                        color = Color(0xFF1D1B20)
                                    )
                                }
                            }
                        }
                    }
                }

                // Sección 3B: Lista numerada de Pasos de Preparación
                if (preparacionTexto.isNotBlank()) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE8DEF8)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.List,
                                    contentDescription = null,
                                    tint = Color(0xFF6750A4),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Text(
                                text = if (item.tipo == "Receta") "Preparación" else "Guía / Programa",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1D1B20)
                            )
                        }

                        val pasos = preparacionTexto.lines().filter { it.isNotBlank() }
                        pasos.forEachIndexed { index, paso ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F2FA))
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.Top,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    // Insignia numerada
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFE8DEF8)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${index + 1}",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF6750A4)
                                        )
                                    }
                                    Text(
                                        text = paso.removePrefix("${index + 1}. "),
                                        fontSize = 15.sp,
                                        color = Color(0xFF1D1B20),
                                        lineHeight = 20.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }

                // 4. Temporizador de Práctica (Para recetas)
                if (item.tipo == "Receta") {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3EDF7)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = null,
                                    tint = Color(0xFF6750A4),
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = "Temporizador de práctica",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1D1B20)
                                )
                            }

                            Text(
                                text = "15 minutos. Úsalo mientras mantienes esta pantalla abierta; no reemplaza el tiempo de la receta.",
                                fontSize = 13.sp,
                                color = Color(0xFF49454F),
                                lineHeight = 18.sp
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "%02d:%02d".format(segundos / 60, segundos % 60),
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1D1B20)
                                )

                                if (segundos > 0) {
                                    OutlinedButton(
                                        onClick = vm::cancelar,
                                        shape = RoundedCornerShape(20.dp)
                                    ) { Text("Cancelar") }
                                } else {
                                    Button(
                                        onClick = vm::iniciar,
                                        shape = RoundedCornerShape(20.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4))
                                    ) { Text("Iniciar 15 minutos", fontWeight = FontWeight.Bold) }
                                }
                            }

                            AnimatedVisibility(terminado) {
                                Text("¡Terminó el tiempo de práctica!", color = Color(0xFF6750A4), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // 5. Botón de Agregar / Quitar de Favoritos
                val favorito = item.id in estado.favoritos
                OutlinedButton(
                    onClick = { vm.cambiarFavorito(item.id) },
                    enabled = !estado.guardando,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF6750A4))
                ) {
                    Icon(
                        imageVector = if (favorito) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = null,
                        tint = Color(0xFF6750A4),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = if (favorito) "Quitar de favoritos" else "Agregar a favoritos",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

// Componente de reproductor de video nativo con controles para Compose
@Composable
private fun ReproductorVideoView(videoResId: Int) {
    val context = LocalContext.current
    val videoUri = remember(videoResId) {
        android.net.Uri.parse("android.resource://${context.packageName}/$videoResId")
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        androidx.compose.ui.viewinterop.AndroidView(
            factory = { ctx ->
                android.widget.VideoView(ctx).apply {
                    setVideoURI(videoUri)
                    val mediaController = android.widget.MediaController(ctx)
                    mediaController.setAnchorView(this)
                    setMediaController(mediaController)
                    start()
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}
