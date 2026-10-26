package com.mastermartini.app.ui.screens.detalle

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mastermartini.app.ui.components.*

@Composable
fun DetalleContenidoScreen(id: Int, volver: () -> Unit, vm: DetalleViewModel = viewModel()) {
    val estado by vm.estado.collectAsStateWithLifecycle()
    val segundos by vm.segundos.collectAsStateWithLifecycle()
    val terminado by vm.terminado.collectAsStateWithLifecycle()
    val contenido = estado.contenidos.find { it.id == id }
    AppPage(contenido?.tipo ?: "Detalle", volver) {
        if (estado.cargando) CircularProgressIndicator()
        MensajeError(estado.mensaje)
        if (!estado.cargando && contenido == null) {
            Text("No se encontró el contenido.")
            TextButton(onClick = vm::cargar) { Text("Volver a intentar") }
        }
        contenido?.let {
            Text(it.titulo, style = MaterialTheme.typography.headlineLarge)
            Text("${it.categoria} · ${it.duracion}", style = MaterialTheme.typography.labelLarge)
            Text(it.descripcion)
            Text(it.detalle, style = MaterialTheme.typography.bodyLarge)
            if (it.tipo == "Receta") Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Temporizador de práctica", style = MaterialTheme.typography.titleLarge)
                    Text("15 minutos. Úsalo mientras mantienes esta pantalla abierta; no reemplaza el tiempo de la receta.")
                    Text("%02d:%02d".format(segundos / 60, segundos % 60),
                        style = MaterialTheme.typography.headlineMedium)
                    AnimatedVisibility(terminado) { Text("¡Terminó el tiempo!") }
                    if (segundos > 0) OutlinedButton(onClick = vm::cancelar) { Text("Cancelar temporizador") }
                    else Button(onClick = vm::iniciar) { Text("Iniciar 15 minutos") }
                }
            }
            val favorito = it.id in estado.favoritos
            OutlinedButton(onClick = { vm.cambiarFavorito(it.id) }, enabled = !estado.guardando) {
                Icon(if (favorito) Icons.Default.Favorite else Icons.Default.FavoriteBorder, null)
                Spacer(Modifier.width(8.dp))
                Text(if (favorito) "Quitar de favoritos" else "Agregar a favoritos")
            }
        }
    }
}
