package com.mastermartini.app.ui.screens.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mastermartini.app.data.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Estado de la interfaz para el inicio de sesión.
data class LoginUiState(
    val correo: String = "",
    val password: String = "",
    val errorCorreo: String? = null,
    val errorPassword: String? = null,
    val mensaje: String? = null,
    val cargando: Boolean = false,
    val loginExitoso: Boolean = false
)

// Fábrica para crear LoginViewModel con el repositorio.
class LoginViewModelFactory(
    private val repository: UsuarioRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(repository) as T
        }
        throw IllegalArgumentException("ViewModel no válido")
    }
}

// Administra la lógica del inicio de sesión.
class LoginViewModel(
    private val usuarioRepository: UsuarioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    // Actualiza el valor del correo.
    fun onCorreoChange(valor: String) {
        _uiState.value = _uiState.value.copy(
            correo = valor,
            errorCorreo = null,
            mensaje = null
        )
    }

    // Actualiza el valor de la contraseña.
    fun onPasswordChange(valor: String) {
        _uiState.value = _uiState.value.copy(
            password = valor,
            errorPassword = null,
            mensaje = null
        )
    }

    // Valida que el formulario esté completo y correcto.
    fun validarFormulario(): Boolean {
        val datos = _uiState.value

        val errorCorreo = when {
            datos.correo.isBlank() -> "Ingresa tu correo"
            !Patterns.EMAIL_ADDRESS.matcher(datos.correo.trim()).matches() -> "Ingresa un correo válido"
            else -> null
        }

        val errorPassword = if (datos.password.isBlank()) {
            "Ingresa tu contraseña"
        } else null

        _uiState.value = datos.copy(
            errorCorreo = errorCorreo,
            errorPassword = errorPassword
        )

        return errorCorreo == null && errorPassword == null
    }

    // Consulta si el usuario existe en la base de datos.
    fun iniciarSesion() {
        if (!validarFormulario()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(cargando = true)

            try {
                val usuario = usuarioRepository.obtenerPorCorreo(
                    _uiState.value.correo.trim().lowercase()
                )

                _uiState.value = _uiState.value.copy(
                    cargando = false,
                    loginExitoso = usuario != null,
                    mensaje = if (usuario != null) {
                        "Usuario encontrado"
                    } else {
                        "No existe una cuenta con ese correo"
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    cargando = false,
                    mensaje = "No se pudo comprobar la cuenta"
                )
            }
        }
    }
}