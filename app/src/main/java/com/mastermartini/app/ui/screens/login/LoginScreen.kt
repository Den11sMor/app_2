package com.mastermartini.app.ui.screens.login

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mastermartini.app.ui.components.*

@Composable
fun LoginScreen(onNavigateToRegistro: () -> Unit, onLoginSuccess: () -> Unit,
                vm: LoginViewModel = viewModel()) {
    val estado by vm.estado.collectAsStateWithLifecycle()
    LaunchedEffect(estado.exitoso) { if (estado.exitoso) onLoginSuccess() }
    AppPage("Master Martini") {
        Text("Inicia sesión para continuar", style = MaterialTheme.typography.headlineMedium)
        Text("Tus recetas, cursos e ideas de cocina en un solo lugar.")
        AppTextField(estado.correo, vm::correo, "Correo electrónico", estado.errorCorreo,
            Icons.Default.Email, KeyboardType.Email, habilitado = !estado.cargando)
        AppTextField(estado.clave, vm::clave, "Contraseña", estado.errorClave,
            Icons.Default.Lock, KeyboardType.Password, secreto = true, habilitado = !estado.cargando)
        MensajeError(estado.mensaje)
        AppButton("Iniciar sesión", estado.cargando, accion = vm::ingresar)
        OutlinedButton(onClick = onNavigateToRegistro, enabled = !estado.cargando) {
            Text("Crear una cuenta")
        }
        Text("Las cuentas de este proyecto se guardan solo en este dispositivo.",
            style = MaterialTheme.typography.bodySmall)
    }
}
