package com.mastermartini.app.ui.screens.detalle

import android.app.Application
import android.os.SystemClock
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.mastermartini.app.ui.viewmodels.ContenidoViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DetalleViewModel(application: Application, private val savedState: SavedStateHandle) : ContenidoViewModel(application) {
    // Se guarda la hora de término: no se atrasa al apagar la pantalla o rotar.
    private var fin: Long
        get() = savedState["fin"] ?: 0L
        set(value) { savedState["fin"] = value }
    private val _segundos = MutableStateFlow(restantes())
    val segundos = _segundos.asStateFlow()
    private val _terminado = MutableStateFlow(false)
    val terminado = _terminado.asStateFlow()

    init {
        viewModelScope.launch {
            while (true) {
                _segundos.value = restantes()
                if (fin != 0L && _segundos.value == 0) {
                    fin = 0
                    _terminado.value = true
                }
                delay(250)
            }
        }
    }

    private fun restantes(): Int =
        if (fin == 0L) 0 else ((fin - SystemClock.elapsedRealtime() + 999) / 1000).coerceAtLeast(0).toInt()

    fun iniciar() {
        _terminado.value = false
        fin = SystemClock.elapsedRealtime() + 15 * 60 * 1000
        _segundos.value = restantes()
    }

    fun cancelar() {
        fin = 0
        _segundos.value = 0
        _terminado.value = false
    }
}
