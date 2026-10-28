package com.mastermartini.app.ui.viewmodels

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.mastermartini.app.utils.Validaciones
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class LoginUiState(
    val correo: String = "", val clave: String = "",
    val errorCorreo: String? = null, val errorClave: String? = null,
    val mensaje: String? = null, val cargando: Boolean = false, val exitoso: Boolean = false
)

class LoginViewModel(application: Application) : BaseViewModel(application) {
    private val _estado = MutableStateFlow(LoginUiState())
    val estado = _estado.asStateFlow()

    fun correo(valor: String) { _estado.update { it.copy(correo = valor, errorCorreo = null, mensaje = null) } }
    fun clave(valor: String) { _estado.update { it.copy(clave = valor, errorClave = null, mensaje = null) } }

    fun ingresar() {
        val actual = _estado.value
        if (actual.cargando) return
        val emailError = Validaciones.correo(actual.correo)
        val claveError = if (actual.clave.isBlank()) "Ingresa tu contraseña" else null
        _estado.update { it.copy(errorCorreo = emailError, errorClave = claveError, mensaje = null) }
        if (emailError != null || claveError != null) return
        _estado.update { it.copy(cargando = true) }
        viewModelScope.launch {
            try {
                val correcto = app.usuarios.ingresar(actual.correo, actual.clave)
                _estado.update { it.copy(cargando = false, exitoso = correcto,
                    clave = if (correcto) "" else it.clave,
                    mensaje = if (correcto) null else "Correo o contraseña incorrectos") }
            } catch (e: CancellationException) { throw e
            } catch (_: Exception) {
                _estado.update { it.copy(cargando = false, mensaje = "No se pudo iniciar sesión. Intenta nuevamente.") }
            }
        }
    }
}
