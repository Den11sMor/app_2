package com.mastermartini.app.ui.screens.videos

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mastermartini.app.ui.components.ContenidoListScreen

@Composable
fun VideosScreen(volver: () -> Unit, abrir: (Int) -> Unit, vm: VideosViewModel = viewModel()) {
    ContenidoListScreen("Videos", "Video", false, volver, abrir, vm)
}
