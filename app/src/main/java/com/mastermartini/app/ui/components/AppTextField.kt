package com.mastermartini.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
        shape = RoundedCornerShape(20.dp), // Bordes redondeados elegantes como en el boceto
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color(0xFFFAF7FC),
            focusedContainerColor = Color(0xFFF3EDF7),
            disabledContainerColor = Color(0xFFF5F2F7),
            errorContainerColor = Color(0xFFFEF2F2),
            unfocusedBorderColor = Color(0xFFE7E0EC),
            focusedBorderColor = Color(0xFF6750A4),
            errorBorderColor = Color(0xFFDC2626)
        ),
        leadingIcon = {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = if (error != null) Color(0xFFDC2626) else Color(0xFF6750A4)
            )
        },
        trailingIcon = if (secreto) { {
            IconButton(onClick = { visible = !visible }) {
                Icon(
                    imageVector = if (visible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = if (visible) "Ocultar contraseña" else "Mostrar contraseña",
                    tint = if (error != null) Color(0xFFDC2626) else Color(0xFF6750A4)
                )
            }
        } } else null,
        visualTransformation = if (secreto && !visible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = tipo),
        supportingText = {
            AnimatedVisibility(visible = error != null) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = null,
                        tint = Color(0xFFDC2626),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = error.orEmpty(),
                        color = Color(0xFFDC2626),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    )
}
