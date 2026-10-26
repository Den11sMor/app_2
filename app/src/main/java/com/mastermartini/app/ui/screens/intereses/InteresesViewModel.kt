package com.mastermartini.app.ui.screens.intereses

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.mastermartini.app.ui.viewmodels.BaseViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class InteresesUiState(
    val seleccionados: Set<String> = emptySet(), val mensaje: String? = null,
    val cargando: Boolean = false, val guardado: Boolean = false, val listo: Boolean = false
)

class InteresesViewModel(application: Application) : BaseViewModel(application) {
    val opciones = listOf("Cocina", "Recetas", "Coctelería", "Gastronomía")
    private val _estado = MutableStateFlow(InteresesUiState())
    val estado = _estado.asStateFlow()

    init {
        viewModelScope.launch {
            val usuario = app.usuarios.usuario.first()
            _estado.update { it.copy(seleccionados = usuario?.intereses?.toSet() ?: emptySet(), listo = true) }
        }
    }

    fun seleccionar(opcion: String) {
        _estado.update { it.copy(
            seleccionados = if (opcion in it.seleccionados) it.seleccionados - opcion else it.seleccionados + opcion,
            mensaje = null
        ) }
    }

    fun guardar() {
        val actual = _estado.value
        if (actual.cargando || !actual.listo) return
        if (actual.seleccionados.isEmpty()) {
            _estado.update { it.copy(mensaje = "Selecciona al menos un interés") }
            return
        }
        _estado.update { it.copy(cargando = true) }
        viewModelScope.launch {
            try {
                app.usuarios.guardarIntereses(actual.seleccionados)
                _estado.update { it.copy(cargando = false, guardado = true) }
            } catch (e: CancellationException) { throw e
            } catch (_: Exception) {
                _estado.update { it.copy(cargando = false, mensaje = "No se pudieron guardar tus intereses") }
            }
        }
    }
}
