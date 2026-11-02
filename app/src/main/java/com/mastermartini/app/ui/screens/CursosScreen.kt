package com.mastermartini.app.ui.screens

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mastermartini.app.ui.components.ContenidoListScreen
import com.mastermartini.app.ui.viewmodels.CursosViewModel

@Composable
fun CursosScreen(volver: () -> Unit, abrir: (Int) -> Unit, vm: CursosViewModel = viewModel()) {
    ContenidoListScreen("Cursos", "Curso", false, volver, abrir, vm)
}
