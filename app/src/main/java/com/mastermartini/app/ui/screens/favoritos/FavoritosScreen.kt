package com.mastermartini.app.ui.screens.favoritos

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mastermartini.app.ui.components.ContenidoListScreen

@Composable
fun FavoritosScreen(volver: () -> Unit, abrir: (Int) -> Unit, vm: FavoritosViewModel = viewModel()) {
    ContenidoListScreen("Mis favoritos", null, true, volver, abrir, vm)
}
