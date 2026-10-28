package com.mastermartini.app.ui.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mastermartini.app.ui.components.*
import com.mastermartini.app.ui.viewmodels.LoginViewModel

// Pantalla de Inicio de Sesión adaptada al boceto visual moderno
@Composable
fun LoginScreen(
    onNavigateToRegistro: () -> Unit,
    onLoginSuccess: () -> Unit,
    vm: LoginViewModel = viewModel()
) {
    val estado by vm.estado.collectAsStateWithLifecycle()

    // Navega al inicio cuando el login es exitoso
    LaunchedEffect(estado.exitoso) {
        if (estado.exitoso) onLoginSuccess()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFCF9FC))
    ) {
        // Decoración gráfica superior derecha limpia (sin traslapar los títulos)
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(width = 150.dp, height = 140.dp)
                .clip(RoundedCornerShape(bottomStart = 90.dp))
                .background(Color(0xFFF3EDF7)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.SoupKitchen,
                contentDescription = null,
                tint = Color(0xFFD0BCFF),
                modifier = Modifier
                    .size(70.dp)
                    .offset(x = 12.dp, y = (-8).dp)
            )
        }

        // Contenido principal de la pantalla
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 28.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // 1. Título de la marca "Master Martini"
            Text(
                text = "Master Martini",
                fontSize = 22.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF1D1B20)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 2. Encabezado principal y subtítulo
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Inicia sesión para\ncontinuar",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    lineHeight = 38.sp,
                    color = Color(0xFF1D1B20)
                )

                Text(
                    text = "Tus recetas, cursos e ideas de cocina en un solo lugar.",
                    fontSize = 15.sp,
                    color = Color(0xFF49454F),
                    lineHeight = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 3. Campos del formulario (Correo y Contraseña)
            AppTextField(
                valor = estado.correo,
                cambiar = vm::correo,
                etiqueta = "Correo electrónico",
                error = estado.errorCorreo,
                icono = Icons.Default.Email,
                tipo = KeyboardType.Email,
                habilitado = !estado.cargando
            )

            AppTextField(
                valor = estado.clave,
                cambiar = vm::clave,
                etiqueta = "Contraseña",
                error = estado.errorClave,
                icono = Icons.Default.Lock,
                tipo = KeyboardType.Password,
                secreto = true,
                habilitado = !estado.cargando
            )

            MensajeError(estado.mensaje)

            Spacer(modifier = Modifier.height(8.dp))

            // 4. Botones de acción principales
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Botón Iniciar Sesión (Relleno morado redondeado)
                AppButton(
                    texto = "Iniciar sesión",
                    cargando = estado.cargando,
                    accion = vm::ingresar
                )

                // Botón Crear una cuenta (Contorneado morado redondeado)
                OutlinedButton(
                    onClick = onNavigateToRegistro,
                    enabled = !estado.cargando,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF6750A4))
                ) {
                    Text(
                        text = "Crear una cuenta",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 5. Nota al pie informativa
            Text(
                text = "Las cuentas de este proyecto se guardan solo en este dispositivo.",
                fontSize = 12.sp,
                color = Color(0xFF49454F),
                lineHeight = 16.sp
            )
        }
    }
}
