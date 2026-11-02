package com.mastermartini.app.ui.viewmodels

import android.app.Application
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(application: Application) : BaseViewModel(application) {
    val idInicial = app.usuarios.idActual
    val usuario = app.usuarios.usuario.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    fun cerrarSesion() = app.usuarios.cerrarSesion()
}