package com.mastermartini.app.ui.screens.registro

import android.util.Patterns
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class RegistroUiState (
    val nombre : String = "",
    val correo: String = "",
    val telefono: String = "",
    val tipoNegocio: String = "",
    val tamanoNegocio: String = "",

    val errorNombre: String? = null,
    val errorCorreo: String? = null,
    val errorTelefono: String? = null,
    val errorTipoNegocio: String? = null,
    val errorTamanoNegocio: String? = null
)

class RegistroViewModel : ViewModel(){

    private val _uiState = MutableStateFlow(RegistroUiState())

    val uiState: StateFlow<RegistroUiState> =
        _uiState.asStateFlow()

    //Actualiza los campos y elimina el error anterior
    fun onNombreChange(valor: String){
        _uiState.update {
            it.copy(nombre = valor, errorNombre = null)
        }
    }

    fun onCorreoChange(valor: String){
        _uiState.update {
            it.copy(correo = valor, errorCorreo = null)
        }
    }

    fun onTelefonoChange(valor: String){
        _uiState.update {
            it.copy(telefono = valor, errorTelefono = null)
        }
    }

    fun onTipoNegocioChange(valor: String){
        _uiState.update {
            it.copy(tipoNegocio = valor, errorTipoNegocio = null)
        }
    }

    fun onTamanoNegocioChange(valor: String){
        _uiState.update {
            it.copy(tamanoNegocio = valor, errorTamanoNegocio  = null)
        }
    }

    //Valida que los formularios antes de permitir continuar
    fun validarFormulario(): Boolean {
        val formulario = _uiState.value

        val errorNombre = if (formulario.nombre.isBlank()) {
            "Ingresa tu nombre"
        } else {
            null
        }

        val errorCorreo = when {
            formulario.correo.isBlank() ->
                "Ingresa tu correo electronico"
            !Patterns.EMAIL_ADDRESS.matcher(
                formulario.correo.trim()
            ).matches() ->
                "Ingresa un correo valido"
            else -> null
        }

        val errorTelefono = if (formulario.telefono.isBlank()){
            "Ingresa tu telefono"
        } else {
            null
        }

        val errorTipoNegocio = if (
            formulario.tipoNegocio.isBlank()
        ) {
            "Ingresa el tipo de negocio"
        } else {
            null
        }

        val errorTamanoNegocio = if (
            formulario.tamanoNegocio.isBlank()
        ){
            "Ingrese el tamaño del negocio"
        } else {
            null
        }

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
}