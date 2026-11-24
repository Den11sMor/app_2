package com.mastermartini.app.ui.screens

import androidx.compose.runtime.Composable

@Composable
fun RecetasScreen(volver: () -> Unit, abrir: (Int) -> Unit) {
    ModuloScreen("Receta", "Recetas", volver, abrir)
}
