package com.mastermartini.app.ui.screens.registro

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mastermartini.app.data.repository.UsuarioRepository
import com.mastermartini.app.domain.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Estado de la interfaz para el formulario de registro.
data class RegistroUiState(
    val nombre: String = "",
    val correo: String = "",
    val telefono: String = "",
    val tipoNegocio: String = "",
    val tamanoNegocio: String = "",
    val errorNombre: String? = null,
    val errorCorreo: String? = null,
    val errorTelefono: String? = null,
    val errorTipoNegocio: String? = null,
    val errorTamanoNegocio: String? = null,
    val mensaje: String? = null,
    val cargando: Boolean = false,
    val registroExitoso: Boolean = false
)

// Fábrica para crear RegistroViewModel con el repositorio de usuarios.
class RegistroViewModelFactory(
    private val repository: UsuarioRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegistroViewModel::class.java)) {
            return RegistroViewModel(repository) as T
        }
        throw IllegalArgumentException("ViewModel no válido")
    }
}

// Administra la lógica de negocio y registro de usuarios.
class RegistroViewModel(
    private val usuarioRepository: UsuarioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistroUiState())
    val uiState = _uiState.asStateFlow()

    // Actualiza el nombre ingresado y borra su error.
    fun onNombreChange(valor: String) {
        _uiState.update { it.copy(nombre = valor, errorNombre = null) }
    }

    // Actualiza el correo electrónico y limpia sus errores.
    fun onCorreoChange(valor: String) {
        _uiState.update { it.copy(correo = valor, errorCorreo = null) }
    }

    // Actualiza el teléfono ingresado.
    fun onTelefonoChange(valor: String) {
        _uiState.update { it.copy(telefono = valor, errorTelefono = null) }
    }

    // Actualiza el tipo de negocio ingresado.
    fun onTipoNegocioChange(valor: String) {
        _uiState.update { it.copy(tipoNegocio = valor, errorTipoNegocio = null) }
    }

    // Actualiza el tamaño del negocio ingresado.
    fun onTamanoNegocioChange(valor: String) {
        _uiState.update { it.copy(tamanoNegocio = valor, errorTamanoNegocio = null) }
    }

    // Valida que todos los campos obligatorios tengan información válida.
    fun validarFormulario(): Boolean {
        val datos = _uiState.value

        val errorNombre = if (datos.nombre.isBlank()) "Ingresa tu nombre" else null

        val errorCorreo = when {
            datos.correo.isBlank() -> "Ingresa tu correo electrónico"
            !Patterns.EMAIL_ADDRESS.matcher(datos.correo.trim()).matches() -> "Ingresa un correo válido"
            else -> null
        }

        val errorTelefono = if (datos.telefono.isBlank()) "Ingresa tu teléfono" else null

        val errorTipoNegocio = if (datos.tipoNegocio.isBlank()) "Ingresa el tipo de negocio" else null

        val errorTamanoNegocio = if (datos.tamanoNegocio.isBlank()) "Ingresa el tamaño del negocio" else null

        _uiState.update {
            it.copy(
                errorNombre = errorNombre,
                errorCorreo = errorCorreo,
                errorTelefono = errorTelefono,
                errorTipoNegocio = errorTipoNegocio,
                errorTamanoNegocio = errorTamanoNegocio
            )
        }

        return errorNombre == null &&
                errorCorreo == null &&
                errorTelefono == null &&
                errorTipoNegocio == null &&
                errorTamanoNegocio == null
    }

    // Guarda el nuevo usuario en Room si los datos son correctos.
    fun registrarUsuario() {
        if (!validarFormulario()) return

        val datos = _uiState.value

        viewModelScope.launch {
            _uiState.update {
                it.copy(cargando = true, mensaje = null, registroExitoso = false)
            }

            try {
                val correo = datos.correo.trim().lowercase()
                val existente = usuarioRepository.obtenerPorCorreo(correo)

                if (existente != null) {
                    _uiState.update {
                        it.copy(
                            cargando = false,
                            errorCorreo = "Este correo ya está registrado"
                        )
                    }
                    return@launch
                }

                val usuario = Usuario(
                    nombre = datos.nombre.trim(),
                    correo = correo,
                    telefono = datos.telefono.trim(),
                    tipoNegocio = datos.tipoNegocio.trim(),
                    tamanoNegocio = datos.tamanoNegocio.trim()
                )

                usuarioRepository.insertar(usuario)

                _uiState.update {
                    it.copy(
                        cargando = false,
                        registroExitoso = true,
                        mensaje = "Usuario registrado correctamente"
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        cargando = false,
                        mensaje = "No se pudo registrar el usuario"
                    )
                }
            }
        }
    }
}