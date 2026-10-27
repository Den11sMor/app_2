package com.mastermartini.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.*

@Composable
fun AppTextField(
    valor: String, cambiar: (String) -> Unit, etiqueta: String,
    error: String? = null, icono: ImageVector = Icons.Default.Edit,
    tipo: KeyboardType = KeyboardType.Text, secreto: Boolean = false,
    habilitado: Boolean = true, soloLectura: Boolean = false
) {
    var visible by rememberSaveable { mutableStateOf(false) }
    OutlinedTextField(
        value = valor, onValueChange = cambiar, label = { Text(etiqueta) },
        modifier = Modifier.fillMaxWidth(), singleLine = true,
        enabled = habilitado, readOnly = soloLectura,
        isError = error != null,
        leadingIcon = { Icon(icono, contentDescription = null) },
        trailingIcon = if (secreto) { {
            IconButton(onClick = { visible = !visible }) {
                Icon(if (visible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = if (visible) "Ocultar contraseña" else "Mostrar contraseña")
            }
        } } else null,
        visualTransformation = if (secreto && !visible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = tipo),
        supportingText = {
            AnimatedVisibility(visible = error != null) {
                Text(error.orEmpty(), color = MaterialTheme.colorScheme.error)
            }
        }
    )
}
