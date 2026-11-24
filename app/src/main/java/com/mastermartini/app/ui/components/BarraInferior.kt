package com.mastermartini.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.mastermartini.app.ui.navigation.Routes

// La navegación principal vive fuera de cada pantalla: no desaparece al abrir contenidos.
@Composable
fun BarraInferior(seccion: String, navegar: (String) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)
            .clip(RoundedCornerShape(32.dp)).testTag("barra-inferior"),
        color = Color(0xFFFAF7FC), tonalElevation = 8.dp, shadowElevation = 4.dp,
    ) {
        NavigationBar(containerColor = Color.Transparent, tonalElevation = 0.dp) {
            val opciones = listOf(
                Triple(Routes.HOME, "Inicio", Icons.Filled.Home),
                Triple(Routes.FAVORITOS, "Mis favoritos", Icons.Outlined.FavoriteBorder),
                Triple(Routes.PERFIL, "Mi perfil", Icons.Outlined.Person),
            )
            opciones.forEach { (ruta, titulo, icono) ->
                NavigationBarItem(
                    selected = seccion == ruta,
                    onClick = { navegar(ruta) },
                    icon = { Icon(icono, contentDescription = null) },
                    label = { Text(titulo) },
                    modifier = Modifier.testTag("nav-$ruta"),
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF6750A4),
                        selectedTextColor = Color(0xFF6750A4),
                        unselectedIconColor = Color(0xFF49454F),
                        unselectedTextColor = Color(0xFF49454F),
                    ),
                )
            }
        }
    }
}
