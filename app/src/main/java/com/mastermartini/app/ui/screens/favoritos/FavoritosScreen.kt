package com.mastermartini.app.ui.screens.favoritos

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mastermartini.app.ui.components.ContenidoListScreen

// Pantalla de favoritos adaptada al nuevo diseño del boceto
@Composable
fun FavoritosScreen(
    volver: () -> Unit,
    abrir: (Int) -> Unit,
    onNavigateToRecetas: (() -> Unit)? = null,
    onNavigateToVideos: (() -> Unit)? = null,
    onNavigateToCursos: (() -> Unit)? = null,
    vm: FavoritosViewModel = viewModel()
) {
    ContenidoListScreen(
        titulo = "Mis favoritos",
        tipo = null,
        soloFavoritos = true,
        volver = volver,
        abrir = abrir,
        vm = vm,
        onNavigateToRecetas = onNavigateToRecetas,
        onNavigateToVideos = onNavigateToVideos,
        onNavigateToCursos = onNavigateToCursos
    )
}
