package com.mastermartini.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppPage(titulo: String, volver: (() -> Unit)? = null, contenido: @Composable ColumnScope.() -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(titulo) }, navigationIcon = {
                if (volver != null) IconButton(onClick = volver) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                }
            })
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding)
                .imePadding().verticalScroll(rememberScrollState()).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = contenido
        )
    }
}

@Composable
fun MensajeError(mensaje: String?) {
    androidx.compose.animation.AnimatedVisibility(visible = mensaje != null) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(androidx.compose.material.icons.Icons.Default.Warning,
                contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
            Text(mensaje.orEmpty(), color = MaterialTheme.colorScheme.error)
        }
    }
}
