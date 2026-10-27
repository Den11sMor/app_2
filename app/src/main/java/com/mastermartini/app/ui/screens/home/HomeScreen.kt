package com.mastermartini.app.ui.screens.home

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mastermartini.app.ui.components.*

@Composable
fun HomeScreen(
    onNavigateToRecetas: () -> Unit, onNavigateToVideos: () -> Unit,
    onNavigateToCursos: () -> Unit, onNavigateToFavoritos: () -> Unit,
    onNavigateToPerfil: () -> Unit, vm: HomeViewModel = viewModel()
) {
    val usuario by vm.usuario.collectAsStateWithLifecycle()
    AppPage("Master Martini") {
        Text("Bienvenido${usuario?.nombre?.let { ", $it" }.orEmpty()}",
            style = MaterialTheme.typography.headlineMedium)
        Text("Selecciona el contenido que quieres revisar.")
        usuario?.intereses?.takeIf { it.isNotEmpty() }?.let {
            Text("Tus intereses: ${it.joinToString()}", style = MaterialTheme.typography.bodySmall)
        }
        AppButton("Recetas", accion = onNavigateToRecetas)
        AppButton("Videos · guías de estudio", accion = onNavigateToVideos)
        AppButton("Cursos", accion = onNavigateToCursos)
        OutlinedButton(onClick = onNavigateToFavoritos) { Text("Mis favoritos") }
        OutlinedButton(onClick = onNavigateToPerfil) { Text("Mi perfil") }
    }
}
