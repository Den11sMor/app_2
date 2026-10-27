package com.mastermartini.app.ui.screens.recetas

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mastermartini.app.ui.components.ContenidoListScreen

@Composable
fun RecetasScreen(volver: () -> Unit, abrir: (Int) -> Unit, vm: RecetasViewModel = viewModel()) {
    ContenidoListScreen("Recetas", "Receta", false, volver, abrir, vm)
}
