package com.mastermartini.app.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mastermartini.app.domain.model.Contenido

@Composable
fun ContentCard(contenido: Contenido, favorito: Boolean, habilitado: Boolean,
                abrir: () -> Unit, cambiarFavorito: () -> Unit) {
    Card(Modifier.fillMaxWidth().animateContentSize()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(contenido.titulo, style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
                IconButton(onClick = cambiarFavorito, enabled = habilitado) {
                    Icon(if (favorito) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (favorito) "Quitar de favoritos" else "Agregar a favoritos",
                        tint = if (favorito) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Text("${contenido.categoria} · ${contenido.duracion}", style = MaterialTheme.typography.labelLarge)
            Text(contenido.descripcion)
            Button(onClick = abrir) { Text(contenido.accion()) }
        }
    }
}
