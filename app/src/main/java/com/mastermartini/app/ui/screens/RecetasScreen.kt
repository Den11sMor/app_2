package com.mastermartini.app.ui.screens

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mastermartini.app.ui.components.ContenidoListScreen
import com.mastermartini.app.ui.viewmodels.RecetasViewModel

@Composable
fun RecetasScreen(volver: () -> Unit, abrir: (Int) -> Unit, vm: RecetasViewModel = viewModel()) {
    ContenidoListScreen("Recetas", "Receta", false, volver, abrir, vm)
}
