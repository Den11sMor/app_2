package com.mastermartini.app.ui.viewmodels

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.mastermartini.app.utils.Validaciones
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Estado de la interfaz para el formulario de registro de usuario.
data class RegistroUiState(
    val nombre: String = "",
    val correo: String = "",
    val telefono: String = "",
    val clave: String = "",
    val repetida: String = "",
    val errores: Map<String, String?> = emptyMap(),
    val mensaje: String? = null,
    val cargando: Boolean = false,
    val exitoso: Boolean = false
)

// ViewModel para administrar el registro de una nueva cuenta.
class RegistroViewModel(application: Application) : BaseViewModel(application) {
    private val _estado = MutableStateFlow(RegistroUiState())
    val estado = _estado.asStateFlow()

    // Actualiza un campo específico del formulario y borra su error.
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

    // Valida y registra al usuario en la base de datos Room.
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
                _estado.update {
                    it.copy(
                        cargando = false,
                        mensaje = error,
                        exitoso = error == null,
                        clave = if (error == null) "" else it.clave,
                        repetida = if (error == null) "" else it.repetida
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _estado.update { it.copy(cargando = false, mensaje = "No se pudo guardar la cuenta. Intenta nuevamente.") }
            }
        }
    }
}
