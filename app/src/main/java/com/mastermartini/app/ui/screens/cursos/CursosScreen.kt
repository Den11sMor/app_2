package com.mastermartini.app.ui.screens.cursos

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mastermartini.app.ui.components.ContenidoListScreen

@Composable
fun CursosScreen(volver: () -> Unit, abrir: (Int) -> Unit, vm: CursosViewModel = viewModel()) {
    ContenidoListScreen("Cursos", "Curso", false, volver, abrir, vm)
}
