package com.mastermartini.app.ui.screens.registro

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mastermartini.app.ui.components.*

@Composable
fun RegistroScreen(onRegistroSuccess: () -> Unit, onNavigateToLogin: () -> Unit,
                   vm: RegistroViewModel = viewModel()) {
    val estado by vm.estado.collectAsStateWithLifecycle()
    LaunchedEffect(estado.exitoso) { if (estado.exitoso) onRegistroSuccess() }
    AppPage("Crear cuenta", onNavigateToLogin) {
        Text("Completa tus datos", style = MaterialTheme.typography.headlineMedium)
        Text("Todos los campos son obligatorios.")
        AppTextField(estado.nombre, { vm.cambiar("nombre", it) }, "Nombre",
            estado.errores["nombre"], Icons.Default.Person, habilitado = !estado.cargando)
        AppTextField(estado.correo, { vm.cambiar("correo", it) }, "Correo electrónico",
            estado.errores["correo"], Icons.Default.Email, KeyboardType.Email, habilitado = !estado.cargando)
        AppTextField(estado.telefono, { vm.cambiar("telefono", it) }, "Teléfono",
            estado.errores["telefono"], Icons.Default.Phone, KeyboardType.Phone, habilitado = !estado.cargando)
        AppTextField(estado.clave, { vm.cambiar("clave", it) }, "Contraseña",
            estado.errores["clave"], Icons.Default.Lock, KeyboardType.Password, secreto = true,
            habilitado = !estado.cargando)
        Text("Usa al menos 8 caracteres, con letras y números.", style = MaterialTheme.typography.bodySmall)
        AppTextField(estado.repetida, { vm.cambiar("repetida", it) }, "Repite la contraseña",
            estado.errores["repetida"], Icons.Default.Lock, KeyboardType.Password, secreto = true,
            habilitado = !estado.cargando)
        MensajeError(estado.mensaje)
        AppButton("Crear cuenta", estado.cargando, accion = vm::registrar)
    }
}
