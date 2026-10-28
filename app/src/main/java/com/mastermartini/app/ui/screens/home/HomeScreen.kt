package com.mastermartini.app.ui.screens.home

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Modelo de datos para las tarjetas del carrusel de banners
private data class BannerItem(
    val titulo: String,
    val descripcion: String,
    val icono: ImageVector
)

// Pantalla principal (HomeScreen) adaptada al boceto móvil moderno con carrusel deslizable
@Composable
fun HomeScreen(
    onNavigateToRecetas: () -> Unit,
    onNavigateToVideos: () -> Unit,
    onNavigateToCursos: () -> Unit,
    onNavigateToFavoritos: () -> Unit,
    onNavigateToPerfil: () -> Unit,
    vm: HomeViewModel = viewModel()
) {
    val usuario by vm.usuario.collectAsStateWithLifecycle()

    // Lista de tarjetas navegables del carrusel principal
    val banners = remember {
        listOf(
            BannerItem(
                titulo = "Aprende, cocina\ny crece",
                descripcion = "Recetas, técnicas y cursos para llevar tu pasión por la gastronomía al siguiente nivel.",
                icono = Icons.AutoMirrored.Filled.MenuBook
            ),
            BannerItem(
                titulo = "Nuevas recetas\ncada semana",
                descripcion = "Explora preparaciones exclusivas de pastelería, chocolatería y panadería profesional.",
                icono = Icons.Default.RestaurantMenu
            ),
            BannerItem(
                titulo = "Practica con\ntemporizador",
                descripcion = "Mide tus tiempos de preparación fácilmente dentro de cada receta con nuestro cronómetro.",
                icono = Icons.Default.Timer
            )
        )
    }

    val pagerState = rememberPagerState(pageCount = { banners.size })

    // Carga la foto de perfil en segundo plano si existe una ruta guardada
    val fotoPerfil by produceState<androidx.compose.ui.graphics.ImageBitmap?>(null, usuario?.foto) {
        value = withContext(Dispatchers.IO) {
            val ruta = usuario?.foto.orEmpty()
            if (ruta.isNotBlank()) {
                runCatching { BitmapFactory.decodeFile(ruta)?.asImageBitmap() }.getOrNull()
            } else null
        }
    }

    Scaffold(
        // Barra inferior de navegación flotante/redondeada con 3 pestañas
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .clip(RoundedCornerShape(32.dp)),
                color = Color(0xFFFAF7FC),
                tonalElevation = 8.dp,
                shadowElevation = 4.dp
            ) {
                NavigationBar(
                    containerColor = Color.Transparent,
                    tonalElevation = 0.dp
                ) {
                    // Pestaña Inicio (Seleccionada)
                    NavigationBarItem(
                        selected = true,
                        onClick = { },
                        icon = { Icon(Icons.Filled.Home, contentDescription = "Inicio", tint = Color(0xFF6750A4)) },
                        label = { Text("Inicio", color = Color(0xFF6750A4), fontWeight = FontWeight.Bold) }
                    )

                    // Pestaña Mis Favoritos
                    NavigationBarItem(
                        selected = false,
                        onClick = onNavigateToFavoritos,
                        icon = { Icon(Icons.Outlined.FavoriteBorder, contentDescription = "Mis favoritos", tint = Color(0xFF49454F)) },
                        label = { Text("Mis favoritos", color = Color(0xFF49454F)) }
                    )

                    // Pestaña Mi Perfil
                    NavigationBarItem(
                        selected = false,
                        onClick = onNavigateToPerfil,
                        icon = { Icon(Icons.Outlined.Person, contentDescription = "Mi perfil", tint = Color(0xFF49454F)) },
                        label = { Text("Mi perfil", color = Color(0xFF49454F)) }
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFCF9FC)) // Fondo crema muy claro como en el boceto
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // 1. Encabezado Superior: "Master Martini" + Avatar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Master ",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1D1B20)
                    )
                    Text(
                        text = "Martini",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF1D1B20)
                    )
                }

                // Avatar circular del usuario
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE8DEF8))
                        .clickable { onNavigateToPerfil() },
                    contentAlignment = Alignment.Center
                ) {
                    if (fotoPerfil != null) {
                        Image(
                            bitmap = fotoPerfil!!,
                            contentDescription = "Foto de perfil",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Perfil",
                            tint = Color(0xFF6750A4),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            // 2. Saludo del usuario + Chip de interés
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Bienvenido${usuario?.nombre?.let { ", $it" } ?: ""}",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    color = Color(0xFF1D1B20)
                )

                Text(
                    text = "Aprende algo rico hoy ✨",
                    fontSize = 18.sp,
                    color = Color(0xFF49454F)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Chip de interés del usuario
                val interesTexto = usuario?.intereses?.firstOrNull() ?: "Gastronomía"
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFF3EDF7)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestaurantMenu,
                            contentDescription = null,
                            tint = Color(0xFF6750A4),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Tu interés: ",
                            fontSize = 14.sp,
                            color = Color(0xFF49454F)
                        )
                        Text(
                            text = interesTexto,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6750A4)
                        )
                    }
                }
            }

            // 3. Tarjeta Hero Principal Interactiva con Desplazamiento Horizontal (HorizontalPager)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3EDF7)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxWidth()
                    ) { page ->
                        val item = banners[page]
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = item.titulo,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1D1B20)
                                )

                                Text(
                                    text = item.descripcion,
                                    fontSize = 13.sp,
                                    color = Color(0xFF49454F),
                                    lineHeight = 18.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            // Ilustración/Icono lateral
                            Box(
                                modifier = Modifier
                                    .size(90.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color(0xFFE8DEF8)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = item.icono,
                                    contentDescription = null,
                                    tint = Color(0xFF6750A4),
                                    modifier = Modifier.size(44.dp)
                                )
                            }
                        }
                    }

                    // Indicadores de página dinámicos (Puntos interactivos que cambian al deslizar)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        repeat(banners.size) { index ->
                            val activo = pagerState.currentPage == index
                            Box(
                                modifier = Modifier
                                    .size(if (activo) 10.dp else 8.dp)
                                    .clip(CircleShape)
                                    .background(if (activo) Color(0xFF6750A4) else Color(0xFFE7E0EC))
                            )
                        }
                    }
                }
            }

            // 4. Tarjetas de Categorías de Acción (Recetas, Videos, Cursos)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Tarjeta 1: Recetas
                CategoryCard(
                    titulo = "Recetas",
                    icono = Icons.Default.RestaurantMenu,
                    onClick = onNavigateToRecetas
                )

                // Tarjeta 2: Videos · guías de estudio
                CategoryCard(
                    titulo = "Videos · guías de estudio",
                    icono = Icons.Default.PlayCircle,
                    onClick = onNavigateToVideos
                )

                // Tarjeta 3: Cursos
                CategoryCard(
                    titulo = "Cursos",
                    icono = Icons.Default.School,
                    onClick = onNavigateToCursos
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

// Componente reusable para las tarjetas de categoría
@Composable
private fun CategoryCard(
    titulo: String,
    icono: ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F2FA)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Contenedor violeta cuadrado/redondeado para el icono
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF6750A4)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icono,
                        contentDescription = titulo,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Text(
                    text = titulo,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1D1B20)
                )
            }

            // Flecha indicadora a la derecha
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Ir a $titulo",
                tint = Color(0xFF6750A4),
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
