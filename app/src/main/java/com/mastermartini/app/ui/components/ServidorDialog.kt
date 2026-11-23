package com.mastermartini.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.mastermartini.app.MasterMartiniApplication

// El login y los contenidos editan la misma configuración, por eso comparten este diálogo.
@Composable
fun ServidorDialog(cerrar: () -> Unit, guardado: () -> Unit) {
    val red = (LocalContext.current.applicationContext as MasterMartiniApplication).red
    var url by remember { mutableStateOf(red.url) }
    var usuariosRemotos by remember { mutableStateOf(red.usuariosRemotos) }
    var error by remember { mutableStateOf<String?>(null) }
    AlertDialog(
        onDismissRequest = cerrar,
        title = { Text("Conexión del servidor") },
        text = {
            Column(
                Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    "Servidor remoto: HTTPS. Por USB: http://127.0.0.1:8080/. Emulador: http://10.0.2.2:8080/."
                )
                OutlinedTextField(
                    url,
                    { url = it },
                    label = { Text("URL del backend") },
                    singleLine = true,
                )
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Cuentas en servidor", modifier = Modifier.weight(1f))
                    Switch(checked = usuariosRemotos, onCheckedChange = { usuariosRemotos = it })
                }
                Text(
                    if (usuariosRemotos)
                        "El registro y el login necesitan el módulo de usuarios del servidor."
                    else
                        "Las cuentas de práctica y el catálogo local funcionan en este teléfono. La gestión del servidor necesita iniciar sesión remota.",
                    style = MaterialTheme.typography.bodySmall,
                )
                error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    try {
                        red.guardar(url, usuariosRemotos)
                        guardado()
                    } catch (e: IllegalArgumentException) {
                        error = e.message
                    }
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = { TextButton(onClick = cerrar) { Text("Cancelar") } },
    )
}
