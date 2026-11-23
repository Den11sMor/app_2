package com.mastermartini.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.mastermartini.app.MasterMartiniApplication
import com.mastermartini.app.ui.viewmodels.RecetaExternaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecetaExternaScreen(volver: () -> Unit) {
    val app = LocalContext.current.applicationContext as MasterMartiniApplication
    val vm: RecetaExternaViewModel =
        viewModel(
            factory =
                object : ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(modelClass: Class<T>) =
                        RecetaExternaViewModel(app.red.recetasExternas()) as T
                }
        )
    val estado by vm.estado.collectAsStateWithLifecycle()
    var consulta by rememberSaveable { mutableStateOf("cake") }
    val atras: () -> Unit = { if (estado.detalle != null) vm.cerrarDetalle() else volver() }
    androidx.activity.compose.BackHandler(enabled = estado.detalle != null) { vm.cerrarDetalle() }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Explorar recetas") },
                navigationIcon = {
                    IconButton(onClick = atras) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                },
            )
        }
    ) { padding ->
        LazyColumn(
            Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Text(
                    "Recetas reales de TheMealDB · API externa",
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    "Contenido original en inglés. Busca cake, chocolate o pasta. Requiere internet."
                )
            }
            if (estado.cargando) item { LinearProgressIndicator(Modifier.fillMaxWidth()) }
            estado.error?.let { mensaje ->
                item {
                    Text(mensaje, color = MaterialTheme.colorScheme.error)
                    TextButton(onClick = { vm.buscar(consulta) }) { Text("Reintentar") }
                }
            }
            val detalle = estado.detalle
            if (detalle != null) {
                item {
                    AsyncImage(
                        detalle.strMealThumb,
                        detalle.strMeal,
                        Modifier.fillMaxWidth().height(220.dp),
                        contentScale = ContentScale.Crop,
                    )
                    Text(detalle.strMeal, style = MaterialTheme.typography.headlineMedium)
                    Text("${detalle.strCategory.orEmpty()} · ${detalle.strArea.orEmpty()}")
                    Text(detalle.strInstructions.orEmpty())
                }
            } else {
                item {
                    OutlinedTextField(
                        consulta,
                        { consulta = it },
                        label = { Text("Nombre de receta en inglés") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                    Button(onClick = { vm.buscar(consulta) }, enabled = !estado.cargando) {
                        Text("Buscar en TheMealDB")
                    }
                }
                if (!estado.cargando && estado.error == null && estado.lista.isEmpty())
                    item { Text("No encontramos recetas con ese nombre.") }
                items(estado.lista, key = { it.idMeal }) { receta ->
                    ElevatedCard(
                        onClick = { vm.consultar(receta.idMeal) },
                        enabled = !estado.cargando,
                    ) {
                        AsyncImage(
                            receta.strMealThumb,
                            receta.strMeal,
                            Modifier.fillMaxWidth().height(180.dp),
                            contentScale = ContentScale.Crop,
                        )
                        Column(Modifier.padding(16.dp)) {
                            Text(receta.strMeal, style = MaterialTheme.typography.titleLarge)
                            Text("${receta.strCategory.orEmpty()} · ${receta.strArea.orEmpty()}")
                            Text("Ver preparación")
                        }
                    }
                }
            }
        }
    }
}
