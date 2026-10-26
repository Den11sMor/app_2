package com.mastermartini.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mastermartini.app.ui.viewmodels.ContenidoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContenidoListScreen(titulo: String, tipo: String?, soloFavoritos: Boolean,
                        volver: () -> Unit, abrir: (Int) -> Unit, vm: ContenidoViewModel) {
    val estado by vm.estado.collectAsStateWithLifecycle()
    val busqueda by vm.consulta.collectAsStateWithLifecycle()
    val lista = estado.contenidos.filter {
        (tipo == null || it.tipo == tipo) &&
            (!soloFavoritos || it.id in estado.favoritos) &&
            (it.titulo.contains(busqueda.trim(), ignoreCase = true) ||
                it.categoria.contains(busqueda.trim(), ignoreCase = true))
    }
    Scaffold(topBar = {
        TopAppBar(title = { Text(titulo) }, navigationIcon = {
            IconButton(onClick = volver) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") }
        })
    }) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                AppTextField(busqueda, vm::buscar, "Buscar por nombre o categoría", icono = Icons.Default.Search)
            }
            if (tipo == "Video") item {
                Text("Guías de estudio locales. Los archivos de video aún no están incluidos.",
                    style = MaterialTheme.typography.bodySmall)
            }
            if (estado.cargando) item { CircularProgressIndicator() }
            if (estado.mensaje != null) item {
                MensajeError(estado.mensaje)
                TextButton(onClick = vm::cargar) { Text("Volver a intentar") }
            }
            if (!estado.cargando && lista.isEmpty()) item {
                Text(if (busqueda.isNotBlank()) "No encontramos coincidencias."
                    else if (soloFavoritos) "Todavía no guardaste favoritos. Usa el corazón en el catálogo."
                    else "No hay contenidos disponibles.")
            }
            items(lista, key = { it.id }) { contenido ->
                ContentCard(contenido, contenido.id in estado.favoritos, !estado.guardando,
                    { abrir(contenido.id) }, { vm.cambiarFavorito(contenido.id) })
            }
        }
    }
}
