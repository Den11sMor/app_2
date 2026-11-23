package com.mastermartini.app.ui.screens

import androidx.compose.runtime.Composable

@Composable
fun CursosScreen(volver: () -> Unit, abrir: (Int) -> Unit) {
    ModuloScreen("Curso", "Cursos", volver, abrir)
}
