package com.mastermartini.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mastermartini.app.ui.viewmodels.ContenidoViewModel

// Pantalla genérica para listar contenidos (Recetas, Videos, Cursos o Favoritos)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContenidoListScreen(
    titulo: String,
    tipo: String?,
    soloFavoritos: Boolean,
    volver: () -> Unit,
    abrir: (Int) -> Unit,
    vm: ContenidoViewModel,
    onNavigateToRecetas: (() -> Unit)? = null,
    onNavigateToVideos: (() -> Unit)? = null,
    onNavigateToCursos: (() -> Unit)? = null
) {
    val estado by vm.estado.collectAsStateWithLifecycle()
    val busqueda by vm.consulta.collectAsStateWithLifecycle()
    val lista = estado.contenidos.filter {
        (tipo == null || it.tipo == tipo) &&
            (!soloFavoritos || it.id in estado.favoritos) &&
            (it.titulo.contains(busqueda.trim(), ignoreCase = true) ||
                it.categoria.contains(busqueda.trim(), ignoreCase = true))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = titulo,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1D1B20)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = volver) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color(0xFF1D1B20)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFCF9FC))
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFCF9FC))
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Campo de búsqueda con ícono de lupa y esquinas suaves
            item {
                AppTextField(
                    valor = busqueda,
                    cambiar = vm::buscar,
                    etiqueta = "Buscar por nombre o categoría",
                    icono = Icons.Default.Search
                )
            }

            if (tipo == "Video") {
                item {
                    Text(
                        text = "Guías de estudio locales. Los archivos de video aún no están incluidos.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF49454F)
                    )
                }
            }

            if (estado.cargando) {
                item {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF6750A4))
                    }
                }
            }

            if (estado.mensaje != null) {
                item {
                    MensajeError(estado.mensaje)
                    TextButton(onClick = vm::cargar) { Text("Volver a intentar") }
                }
            }

            // Estado vacío para Favoritos (calculado exactamente como el boceto)
            if (!estado.cargando && lista.isEmpty()) {
                item {
                    if (soloFavoritos && busqueda.isBlank()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            shape = RoundedCornerShape(28.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F2FA)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(28.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(20.dp)
                            ) {
                                // Gráfico de Corazón Central
                                Box(
                                    modifier = Modifier
                                        .size(130.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFE8DEF8)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Favorite,
                                        contentDescription = "Favoritos",
                                        tint = Color(0xFF6750A4),
                                        modifier = Modifier.size(64.dp)
                                    )
                                }

                                // Mensaje de estado vacío
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "Todavía no guardaste favoritos.",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1D1B20),
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = "Usa el corazón en el catálogo.",
                                        fontSize = 15.sp,
                                        color = Color(0xFF49454F),
                                        textAlign = TextAlign.Center
                                    )
                                }

                                // Botones de accesos directos a Recetas, Videos y Cursos
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { onNavigateToRecetas?.invoke() ?: volver() },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(18.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF6750A4)),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 10.dp)
                                    ) {
                                        Icon(Icons.Default.RestaurantMenu, null, modifier = Modifier.size(16.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("Recetas", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    }

                                    OutlinedButton(
                                        onClick = { onNavigateToVideos?.invoke() ?: volver() },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(18.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF6750A4)),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 10.dp)
                                    ) {
                                        Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(16.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("Videos", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    }

                                    OutlinedButton(
                                        onClick = { onNavigateToCursos?.invoke() ?: volver() },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(18.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF6750A4)),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 10.dp)
                                    ) {
                                        Icon(Icons.Default.School, null, modifier = Modifier.size(16.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("Cursos", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    } else {
                        Text(
                            text = if (busqueda.isNotBlank()) "No encontramos coincidencias." else "No hay contenidos disponibles.",
                            color = Color(0xFF49454F),
                            fontSize = 15.sp,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                }
            }

            // Lista de tarjetas de contenido cuando hay elementos
            items(lista, key = { it.id }) { contenido ->
                ContentCard(
                    contenido = contenido,
                    favorito = contenido.id in estado.favoritos,
                    habilitado = !estado.guardando,
                    abrir = { abrir(contenido.id) },
                    cambiarFavorito = { vm.cambiarFavorito(contenido.id) }
                )
            }
        }
    }
}
