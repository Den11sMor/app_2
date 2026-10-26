package com.mastermartini.app.ui.screens.home

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.mastermartini.app.ui.viewmodels.BaseViewModel
import kotlinx.coroutines.flow.*

class HomeViewModel(application: Application) : BaseViewModel(application) {
    val idInicial = app.usuarios.idActual
    val usuario = app.usuarios.usuario.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    fun cerrarSesion() = app.usuarios.cerrarSesion()
}
