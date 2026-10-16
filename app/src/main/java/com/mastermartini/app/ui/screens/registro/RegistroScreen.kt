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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

// Pantalla de registro conectada al ViewModel.
@Composable
fun RegistroScreen(
    onRegistroSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    registroViewModel: RegistroViewModel = viewModel()
) {
    val uiState by registroViewModel.uiState.collectAsState()

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

        // Campo de nombre con validación.
        OutlinedTextField(
            value = uiState.nombre,
            onValueChange = registroViewModel::onNombreChange,
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = uiState.errorNombre != null,
            supportingText = {
                uiState.errorNombre?.let { Text(it) }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Campo de correo electrónico.
        OutlinedTextField(
            value = uiState.correo,
            onValueChange = registroViewModel::onCorreoChange,
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = uiState.errorCorreo != null,
            supportingText = {
                uiState.errorCorreo?.let { Text(it) }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Campo de teléfono.
        OutlinedTextField(
            value = uiState.telefono,
            onValueChange = registroViewModel::onTelefonoChange,
            label = { Text("Teléfono") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = uiState.errorTelefono != null,
            supportingText = {
                uiState.errorTelefono?.let { Text(it) }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Campo de tipo de negocio.
        OutlinedTextField(
            value = uiState.tipoNegocio,
            onValueChange = registroViewModel::onTipoNegocioChange,
            label = { Text("Tipo de negocio") },
            placeholder = { Text("Ej: Restaurante, cafetería...") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = uiState.errorTipoNegocio != null,
            supportingText = {
                uiState.errorTipoNegocio?.let { Text(it) }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Campo de tamaño de negocio.
        OutlinedTextField(
            value = uiState.tamanoNegocio,
            onValueChange = registroViewModel::onTamanoNegocioChange,
            label = { Text("Tamaño del negocio") },
            placeholder = { Text("Ej: Pequeño, mediano...") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = uiState.errorTamanoNegocio != null,
            supportingText = {
                uiState.errorTamanoNegocio?.let { Text(it) }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Valida los datos antes de continuar.
        Button(
            onClick = {
                if (registroViewModel.validarFormulario()) {
                    onRegistroSuccess()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continuar")
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Permite volver al inicio de sesión.
        OutlinedButton(
            onClick = onNavigateToLogin,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver al inicio de sesión")
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}