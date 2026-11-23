package com.mastermartini.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mastermartini.app.MasterMartiniApplication
import com.mastermartini.app.ui.components.ContenidoListScreen
import com.mastermartini.app.ui.components.ReproductorVideo
import com.mastermartini.app.ui.viewmodels.*

@Composable
fun ModuloScreen(tipo: String, titulo: String, volver: () -> Unit, abrir: (Int) -> Unit) {
    var modo by rememberSaveable { mutableStateOf("local") }
    when (modo) {
        "servidor" -> GestionContenidoScreen(tipo, titulo, { modo = "local" })
        "externa" -> RecetaExternaScreen { modo = "local" }
        else -> {
            val vm: ContenidoViewModel = viewModel()
            ContenidoListScreen(
                titulo,
                tipo,
                false,
                volver,
                abrir,
                vm,
                administrar = { modo = "servidor" },
                explorar = if (tipo == "Receta") ({ modo = "externa" }) else null,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionContenidoScreen(tipo: String, titulo: String, volver: () -> Unit) {
    val app = LocalContext.current.applicationContext as MasterMartiniApplication
    var revision by rememberSaveable { mutableIntStateOf(0) }
    var configurar by remember { mutableStateOf(false) }
    var confirmarEliminar by remember { mutableStateOf(false) }
    var busqueda by rememberSaveable { mutableStateOf("") }
    val vm: ModuloViewModel =
        viewModel(
            key = "remoto-$tipo-${app.red.url.hashCode()}-${app.red.clave.hashCode()}-$revision",
            factory =
                object : ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        val repository = app.red.repositorio(tipo)
                        return when (tipo) {
                            "Curso" -> CursosViewModel(repository)
                            "Video" -> VideosViewModel(repository)
                            else -> RecetasViewModel(repository)
                        }
                            as T
                    }
                },
        )
    val estado by vm.estado.collectAsStateWithLifecycle()
    val atras: () -> Unit = {
        if (estado.formulario != null || estado.seleccionado != null) vm.cerrar() else volver()
    }
    androidx.activity.compose.BackHandler(
        enabled = estado.formulario != null || estado.seleccionado != null
    ) {
        vm.cerrar()
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (estado.formulario != null) "Formulario · $tipo"
                        else "$titulo · Servidor"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = atras, enabled = !estado.ocupado) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { configurar = true }, enabled = !estado.ocupado) {
                        Icon(Icons.Default.Settings, "Configurar servidor")
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
                Text("Conectado a: ${app.red.url}", style = MaterialTheme.typography.labelSmall)
                Text(
                    "Los cambios de esta sección se guardan en Spring Boot y MySQL.",
                    style = MaterialTheme.typography.bodySmall,
                )
                if (estado.ocupado) LinearProgressIndicator(Modifier.fillMaxWidth())
                estado.mensaje?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                estado.exito?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
            }
            val formulario = estado.formulario
            if (formulario != null) {
                item {
                    Text(
                        if (estado.seleccionado == null) "Crear $tipo" else "Editar $tipo",
                        style = MaterialTheme.typography.headlineSmall,
                    )
                }
                val campos =
                    listOf(
                        "titulo" to formulario.titulo,
                        "categoria" to formulario.categoria,
                        "duracion" to formulario.duracion,
                        "descripcion" to formulario.descripcion,
                        "detalle" to formulario.detalle,
                    ) +
                        if (tipo == "Video") listOf("videoUrl" to formulario.videoUrl)
                        else emptyList()
                items(campos, key = { it.first }) { (campo, valor) ->
                    val etiqueta =
                        when (campo) {
                            "titulo" -> "Título"
                            "categoria" -> "Categoría"
                            "duracion" -> "Duración (ej. 30 minutos)"
                            "descripcion" -> "Descripción"
                            "videoUrl" -> "Enlace directo al video MP4 (HTTPS)"
                            else ->
                                if (tipo == "Receta") "Ingredientes y preparación"
                                else if (tipo == "Curso") "Programa y unidades"
                                else "Guía del video"
                        }
                    OutlinedTextField(
                        value = valor,
                        onValueChange = { vm.cambiar(campo, it) },
                        label = { Text(etiqueta) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !estado.ocupado,
                        isError = estado.errores.containsKey(campo),
                        supportingText = { estado.errores[campo]?.let { Text(it) } },
                        minLines =
                            if (campo == "detalle") 5 else if (campo == "descripcion") 2 else 1,
                        singleLine = campo !in listOf("detalle", "descripcion"),
                    )
                }
                item {
                    Button(
                        onClick = vm::guardar,
                        enabled = !estado.ocupado,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Guardar en servidor")
                    }
                    TextButton(onClick = vm::cerrar, enabled = !estado.ocupado) { Text("Cancelar") }
                }
            } else if (estado.seleccionado != null) {
                val item = estado.seleccionado!!
                item {
                    Text(item.titulo, style = MaterialTheme.typography.headlineMedium)
                    Text("${item.categoria} · ${item.duracion}")
                }
                if (tipo == "Video" && item.videoUrl.isNotBlank())
                    item {
                        key(item.id, item.videoUrl) { ReproductorVideo(videoUrl = item.videoUrl) }
                    }
                item {
                    Text(item.descripcion)
                    HorizontalDivider(Modifier.padding(vertical = 16.dp))
                    Text(item.detalle)
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(onClick = vm::editar, enabled = !estado.ocupado) {
                            Icon(Icons.Default.Edit, null)
                            Text("Editar")
                        }
                        OutlinedButton(
                            onClick = { confirmarEliminar = true },
                            enabled = !estado.ocupado,
                        ) {
                            Icon(Icons.Default.Delete, null)
                            Text("Eliminar")
                        }
                    }
                }
            } else {
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(onClick = vm::nuevo, enabled = !estado.ocupado) {
                            Icon(Icons.Default.Add, null)
                            Text("Crear $tipo")
                        }
                        OutlinedButton(onClick = vm::cargar, enabled = !estado.ocupado) {
                            Text("Actualizar")
                        }
                    }
                    OutlinedTextField(
                        busqueda,
                        { busqueda = it },
                        label = { Text("Buscar por nombre o categoría") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                val lista =
                    estado.lista.filter {
                        it.titulo.contains(busqueda, true) || it.categoria.contains(busqueda, true)
                    }
                if (!estado.ocupado && lista.isEmpty())
                    item {
                        Text(
                            if (estado.mensaje == null)
                                "No hay registros. Crea el primero o cambia tu búsqueda."
                            else "Configura el servidor y pulsa Actualizar."
                        )
                    }
                items(lista, key = { it.id }) { item ->
                    ElevatedCard(
                        onClick = { vm.consultar(item.id) },
                        enabled = !estado.ocupado,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(
                            Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Icon(
                                when (tipo) {
                                    "Curso" -> Icons.Default.School
                                    "Video" -> Icons.Default.PlayCircle
                                    else -> Icons.Default.RestaurantMenu
                                },
                                null,
                            )
                            Text(item.titulo, style = MaterialTheme.typography.titleLarge)
                            Text("${item.categoria} · ${item.duracion}")
                            Text(item.descripcion)
                            Text("Ver detalles", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
    if (confirmarEliminar)
        AlertDialog(
            onDismissRequest = { confirmarEliminar = false },
            title = { Text("¿Eliminar contenido?") },
            text = { Text("Se eliminará del servidor. Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        confirmarEliminar = false
                        vm.eliminar()
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmarEliminar = false }) { Text("Cancelar") }
            },
        )
    if (configurar) {
        var url by remember { mutableStateOf(app.red.url) }
        var clave by remember { mutableStateOf(app.red.clave) }
        var error by remember { mutableStateOf<String?>(null) }
        AlertDialog(
            onDismissRequest = { configurar = false },
            title = { Text("Conexión con Spring Boot") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Usa HTTPS para un servidor remoto. Por USB: http://127.0.0.1:8080/ y adb reverse tcp:8080 tcp:8080."
                    )
                    OutlinedTextField(
                        url,
                        { url = it },
                        label = { Text("URL del backend") },
                        singleLine = true,
                    )
                    OutlinedTextField(
                        clave,
                        { clave = it },
                        label = { Text("Clave API (si está configurada)") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                    )
                    error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        try {
                            app.red.guardar(url, clave)
                            revision++
                            configurar = false
                        } catch (e: IllegalArgumentException) {
                            error = e.message
                        }
                    }
                ) {
                    Text("Guardar y conectar")
                }
            },
            dismissButton = { TextButton(onClick = { configurar = false }) { Text("Cancelar") } },
        )
    }
}
