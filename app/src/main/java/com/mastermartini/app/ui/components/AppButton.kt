package com.mastermartini.app.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AppButton(texto: String, cargando: Boolean = false, habilitado: Boolean = true, accion: () -> Unit) {
    Button(
        onClick = accion,
        enabled = habilitado && !cargando,
        shape = RoundedCornerShape(24.dp), // Botón completamente redondeado según el boceto
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4)),
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .animateContentSize()
    ) {
        if (cargando) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = Color.White
            )
            Spacer(Modifier.width(8.dp))
            Text("Guardando…", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        } else {
            Text(texto, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}
