package com.mastermartini.app.ui.screens

import androidx.compose.runtime.Composable

@Composable
fun VideosScreen(volver: () -> Unit, abrir: (Int) -> Unit) {
    ModuloScreen("Video", "Videos", volver, abrir)
}
