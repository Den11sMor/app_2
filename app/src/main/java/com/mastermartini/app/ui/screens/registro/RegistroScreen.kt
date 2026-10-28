package com.mastermartini.app.ui.screens.registro

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mastermartini.app.ui.components.*
import com.mastermartini.app.ui.screens.registro.RegistroViewModel

// Pantalla de Registro de Usuario adaptada al boceto visual moderno
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    onRegistroSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    vm: RegistroViewModel = viewModel()
) {
    val estado by vm.estado.collectAsStateWithLifecycle()

    // Navega cuando el registro finaliza con éxito
    LaunchedEffect(estado.exitoso) {
        if (estado.exitoso) onRegistroSuccess()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Crear cuenta",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1D1B20)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateToLogin,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color(0xFFF3EDF7))
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver al inicio de sesión",
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
                .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Encabezado y descripción
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Completa tus datos",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1D1B20)
                )

                Text(
                    text = "Todos los campos son obligatorios.",
                    fontSize = 15.sp,
                    color = Color(0xFF49454F)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Campo Nombre
            AppTextField(
                valor = estado.nombre,
                cambiar = { vm.cambiar("nombre", it) },
                etiqueta = "Nombre",
                error = estado.errores["nombre"],
                icono = Icons.Default.Person,
                habilitado = !estado.cargando
            )

            // Campo Correo electrónico
            AppTextField(
                valor = estado.correo,
                cambiar = { vm.cambiar("correo", it) },
                etiqueta = "Correo electrónico",
                error = estado.errores["correo"],
                icono = Icons.Default.Email,
                tipo = KeyboardType.Email,
                habilitado = !estado.cargando
            )

            // Campo Teléfono
            AppTextField(
                valor = estado.telefono,
                cambiar = { vm.cambiar("telefono", it) },
                etiqueta = "Teléfono",
                error = estado.errores["telefono"],
                icono = Icons.Default.Phone,
                tipo = KeyboardType.Phone,
                habilitado = !estado.cargando
            )

            // Campo Contraseña
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                AppTextField(
                    valor = estado.clave,
                    cambiar = { vm.cambiar("clave", it) },
                    etiqueta = "Contraseña",
                    error = estado.errores["clave"],
                    icono = Icons.Default.Lock,
                    tipo = KeyboardType.Password,
                    secreto = true,
                    habilitado = !estado.cargando
                )

                Text(
                    text = "Usa al menos 8 caracteres, con letras y números.",
                    fontSize = 13.sp,
                    color = Color(0xFF49454F),
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            // Campo Repite la contraseña
            AppTextField(
                valor = estado.repetida,
                cambiar = { vm.cambiar("repetida", it) },
                etiqueta = "Repite la contraseña",
                error = estado.errores["repetida"],
                icono = Icons.Default.Lock,
                tipo = KeyboardType.Password,
                secreto = true,
                habilitado = !estado.cargando
            )

            MensajeError(estado.mensaje)

            Spacer(modifier = Modifier.height(8.dp))

            // Botón Crear cuenta
            AppButton(
                texto = "Crear cuenta",
                cargando = estado.cargando,
                accion = vm::registrar
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
