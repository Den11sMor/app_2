package com.mastermartini.app.ui.screens.intereses

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mastermartini.app.ui.components.*

// Modelo interno para mapear cada opción de interés con su subtítulo e icono
private data class OpcionInfo(
    val titulo: String,
    val descripcion: String,
    val icono: ImageVector
)

// Pantalla de Selección de Intereses adaptada al boceto moderno de tarjetas
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InteresesScreen(
    onContinue: () -> Unit,
    volver: (() -> Unit)? = null,
    vm: InteresesViewModel = viewModel()
) {
    val estado by vm.estado.collectAsStateWithLifecycle()

    // Opciones con sus respectivas descripciones e íconos
    val listaOpciones = remember {
        listOf(
            OpcionInfo("Cocina", "Técnicas e ingredientes", Icons.Default.SoupKitchen),
            OpcionInfo("Recetas", "Preparaciones paso a paso", Icons.AutoMirrored.Filled.MenuBook),
            OpcionInfo("Coctelería", "Bebidas y combinaciones", Icons.Default.LocalBar),
            OpcionInfo("Gastronomía", "Sabores y cultura culinaria", Icons.Default.DinnerDining)
        )
    }

    LaunchedEffect(estado.guardado) {
        if (estado.guardado) onContinue()
    }

    Scaffold(
        topBar = {
            if (volver != null) {
                TopAppBar(
                    title = { Text("") },
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
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFCF9FC))
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Título principal y subtítulos
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Tus intereses",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1D1B20)
                )

                Text(
                    text = "Selecciona los contenidos que más te interesan",
                    fontSize = 15.sp,
                    color = Color(0xFF49454F)
                )

                Text(
                    text = "Puedes cambiarlos después desde tu perfil.",
                    fontSize = 15.sp,
                    color = Color(0xFF49454F)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Tarjetas de opciones de interés
            listaOpciones.forEach { opcion ->
                val seleccionado = opcion.titulo in estado.seleccionados

                // Animación suave de color de fondo y borde al seleccionar
                val fondoColor by animateColorAsState(
                    if (seleccionado) Color(0xFFF3EDF7) else Color(0xFFFAF7FC),
                    label = "fondoColor"
                )
                val bordeColor by animateColorAsState(
                    if (seleccionado) Color(0xFF6750A4) else Color(0xFFE7E0EC),
                    label = "bordeColor"
                )

                Card(
                    onClick = { vm.seleccionar(opcion.titulo) },
                    enabled = !estado.cargando && estado.listo,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = fondoColor),
                    border = BorderStroke(1.5.dp, bordeColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            // Círculo morado claro con ícono temático
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE8DEF8)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = opcion.icono,
                                    contentDescription = opcion.titulo,
                                    tint = Color(0xFF6750A4),
                                    modifier = Modifier.size(26.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = opcion.titulo,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1D1B20)
                                )
                                Text(
                                    text = opcion.descripcion,
                                    fontSize = 13.sp,
                                    color = Color(0xFF49454F)
                                )
                            }
                        }

                        // Indicador de selección a la derecha (Círculo marcado con ✓ o desmarcado ○)
                        if (seleccionado) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Seleccionado",
                                tint = Color(0xFF6750A4),
                                modifier = Modifier.size(26.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.Circle,
                                contentDescription = "No seleccionado",
                                tint = Color(0xFFCAC4D0),
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                }
            }

            MensajeError(estado.mensaje)

            Spacer(modifier = Modifier.height(10.dp))

            // Botón Guardar y continuar
            AppButton(
                texto = "Guardar y continuar",
                cargando = estado.cargando,
                habilitado = estado.listo,
                accion = vm::guardar
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
