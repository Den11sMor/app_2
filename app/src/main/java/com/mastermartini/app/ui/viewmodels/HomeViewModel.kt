package com.mastermartini.app.ui.viewmodels

import android.app.Application
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

// ViewModel para administrar los datos del usuario y sesión en la pantalla principal.
class HomeViewModel(application: Application) : BaseViewModel(application) {
    val idInicial = app.usuarios.idActual
    val usuario = app.usuarios.usuario.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Cierra la sesión activa del usuario.
    fun cerrarSesion() = app.usuarios.cerrarSesion()
}
