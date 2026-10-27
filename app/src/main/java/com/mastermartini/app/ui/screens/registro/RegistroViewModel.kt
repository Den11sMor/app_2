package com.mastermartini.app.ui.screens.registro

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.mastermartini.app.ui.viewmodels.BaseViewModel
import com.mastermartini.app.utils.Validaciones
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class RegistroUiState(
    val nombre: String = "", val correo: String = "", val telefono: String = "",
    val clave: String = "", val repetida: String = "",
    val errores: Map<String, String?> = emptyMap(),
    val mensaje: String? = null, val cargando: Boolean = false, val exitoso: Boolean = false
)

class RegistroViewModel(application: Application) : BaseViewModel(application) {
    private val _estado = MutableStateFlow(RegistroUiState())
    val estado = _estado.asStateFlow()

    fun cambiar(campo: String, valor: String) {
        _estado.update {
            val siguiente = when (campo) {
                "nombre" -> it.copy(nombre = valor)
                "correo" -> it.copy(correo = valor)
                "telefono" -> it.copy(telefono = valor)
                "clave" -> it.copy(clave = valor)
                "repetida" -> it.copy(repetida = valor)
                else -> it
            }
            siguiente.copy(errores = it.errores - campo, mensaje = null)
        }
    }

    fun registrar() {
        val actual = _estado.value
        if (actual.cargando) return
        val errores = mapOf(
            "nombre" to Validaciones.nombre(actual.nombre),
            "correo" to Validaciones.correo(actual.correo),
            "telefono" to Validaciones.telefono(actual.telefono),
            "clave" to Validaciones.clave(actual.clave),
            "repetida" to Validaciones.confirmacion(actual.clave, actual.repetida)
        )
        _estado.update { it.copy(errores = errores, mensaje = null) }
        if (errores.values.any { it != null }) return
        _estado.update { it.copy(cargando = true) }
        viewModelScope.launch {
            try {
                val error = app.usuarios.registrar(actual.nombre, actual.correo, actual.telefono, actual.clave)
                _estado.update { it.copy(cargando = false, mensaje = error, exitoso = error == null,
                    clave = if (error == null) "" else it.clave,
                    repetida = if (error == null) "" else it.repetida) }
            } catch (e: CancellationException) { throw e
            } catch (_: Exception) {
                _estado.update { it.copy(cargando = false, mensaje = "No se pudo guardar la cuenta. Intenta nuevamente.") }
            }
        }
    }
}
