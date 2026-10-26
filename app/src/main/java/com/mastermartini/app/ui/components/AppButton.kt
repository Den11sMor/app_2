package com.mastermartini.app.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AppButton(texto: String, cargando: Boolean = false, habilitado: Boolean = true, accion: () -> Unit) {
    Button(onClick = accion, enabled = habilitado && !cargando,
        modifier = Modifier.fillMaxWidth().animateContentSize()) {
        if (cargando) {
            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp,
                color = LocalContentColor.current)
            Spacer(Modifier.width(8.dp))
            Text("Guardando…")
        } else Text(texto)
    }
}
