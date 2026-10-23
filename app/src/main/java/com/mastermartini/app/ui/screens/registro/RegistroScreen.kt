package com.mastermartini.app.ui.screens.registro

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mastermartini.app.data.local.database.AppDatabase
import com.mastermartini.app.data.repository.UsuarioRepository

// Pantalla para crear una nueva cuenta.
@Composable
fun RegistroScreen(
    onRegistroSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    // Inicializa la base de datos y el repositorio local.
    val context = LocalContext.current.applicationContext
    val database = remember { AppDatabase.obtenerInstancia(context) }
    val repository = remember { UsuarioRepository(database.usuarioDao()) }
    val registroViewModel: RegistroViewModel = viewModel(
        factory = RegistroViewModelFactory(repository)
    )

    // Observa el estado del formulario de registro.
    val uiState by registroViewModel.uiState.collectAsState()

    // Navega cuando el registro se completa con éxito.
    LaunchedEffect(uiState.registroExitoso) {
        if (uiState.registroExitoso) {
            onRegistroSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Crear cuenta",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Completa tus datos para comenzar")

        Spacer(modifier = Modifier.height(24.dp))

        // Campo para ingresar el nombre.
        OutlinedTextField(
            value = uiState.nombre,
            onValueChange = registroViewModel::onNombreChange,
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = uiState.errorNombre != null,
            supportingText = { uiState.errorNombre?.let { Text(it) } }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Campo para ingresar el correo electrónico.
        OutlinedTextField(
            value = uiState.correo,
            onValueChange = registroViewModel::onCorreoChange,
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = uiState.errorCorreo != null,
            supportingText = { uiState.errorCorreo?.let { Text(it) } }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Campo para ingresar el teléfono.
        OutlinedTextField(
            value = uiState.telefono,
            onValueChange = registroViewModel::onTelefonoChange,
            label = { Text("Teléfono") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = uiState.errorTelefono != null,
            supportingText = { uiState.errorTelefono?.let { Text(it) } }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Campo para indicar el tipo de negocio.
        OutlinedTextField(
            value = uiState.tipoNegocio,
            onValueChange = registroViewModel::onTipoNegocioChange,
            label = { Text("Tipo de negocio") },
            placeholder = { Text("Ej: Restaurante, cafetería...") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = uiState.errorTipoNegocio != null,
            supportingText = { uiState.errorTipoNegocio?.let { Text(it) } }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Campo para indicar el tamaño del negocio.
        OutlinedTextField(
            value = uiState.tamanoNegocio,
            onValueChange = registroViewModel::onTamanoNegocioChange,
            label = { Text("Tamaño del negocio") },
            placeholder = { Text("Ej: Pequeño, mediano...") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = uiState.errorTamanoNegocio != null,
            supportingText = { uiState.errorTamanoNegocio?.let { Text(it) } }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Muestra los mensajes de error o confirmación.
        uiState.mensaje?.let { mensaje ->
            Text(
                text = mensaje,
                color = if (uiState.registroExitoso) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                }
            )

            Spacer(modifier = Modifier.height(12.dp))
        }

        // Botón para procesar el registro del usuario.
        Button(
            onClick = registroViewModel::registrarUsuario,
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.cargando
        ) {
            if (uiState.cargando) {
                CircularProgressIndicator()
            } else {
                Text("Crear cuenta")
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Botón para volver al inicio de sesión.
        OutlinedButton(
            onClick = onNavigateToLogin,
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.cargando
        ) {
            Text("Volver al inicio de sesión")
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}