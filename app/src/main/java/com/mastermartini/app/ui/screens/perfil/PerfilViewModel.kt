package com.mastermartini.app.ui.screens.perfil

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.mastermartini.app.ui.viewmodels.BaseViewModel
import com.mastermartini.app.utils.Validaciones
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.File

data class PerfilUiState(
    val nombre: String = "", val correo: String = "", val telefono: String = "",
    val intereses: List<String> = emptyList(), val foto: String = "",
    val errorNombre: String? = null, val errorTelefono: String? = null,
    val mensaje: String? = null, val aviso: String? = null,
    val cargando: Boolean = false, val listo: Boolean = false
)

class PerfilViewModel(application: Application) : BaseViewModel(application) {
    private val _estado = MutableStateFlow(PerfilUiState())
    val estado = _estado.asStateFlow()

    init {
        viewModelScope.launch {
            app.usuarios.usuario.collect { usuario ->
                if (usuario != null) _estado.update {
                    it.copy(nombre = if (it.listo) it.nombre else usuario.nombre,
                        telefono = if (it.listo) it.telefono else usuario.telefono,
                        correo = usuario.correo, foto = usuario.foto,
                        intereses = usuario.intereses, listo = true)
                }
            }
        }
    }

    fun nombre(valor: String) { _estado.update { it.copy(nombre = valor, errorNombre = null, aviso = null) } }
    fun telefono(valor: String) { _estado.update { it.copy(telefono = valor, errorTelefono = null, aviso = null) } }
    fun errorFoto() { _estado.update { it.copy(mensaje = "No se pudo abrir la cámara o el selector de fotos") } }

    fun guardar() {
        val actual = _estado.value
        if (actual.cargando || !actual.listo) return
        val nombreError = Validaciones.nombre(actual.nombre)
        val telefonoError = Validaciones.telefono(actual.telefono)
        _estado.update { it.copy(errorNombre = nombreError, errorTelefono = telefonoError, aviso = null, mensaje = null) }
        if (nombreError != null || telefonoError != null) return
        _estado.update { it.copy(cargando = true) }
        viewModelScope.launch {
            try {
                app.usuarios.guardarPerfil(actual.nombre, actual.telefono)
                _estado.update { it.copy(cargando = false, aviso = "Perfil actualizado") }
            } catch (e: CancellationException) { throw e
            } catch (_: Exception) {
                _estado.update { it.copy(cargando = false, mensaje = "No se pudo guardar el perfil") }
            }
        }
    }

    // Copia y reduce la foto elegida; no depende de permisos permanentes a la galería.
    fun guardarFoto(uri: Uri) {
        if (_estado.value.cargando) return
        _estado.update { it.copy(cargando = true, mensaje = null, aviso = null) }
        viewModelScope.launch {
            try {
                val ruta = withContext(Dispatchers.IO) {
                    val resolver = app.contentResolver
                    val opciones = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                    resolver.openInputStream(uri).use { BitmapFactory.decodeStream(it, null, opciones) }
                    require(opciones.outWidth > 0 && opciones.outHeight > 0)
                    var muestra = 1
                    while (opciones.outWidth / muestra > 1024 || opciones.outHeight / muestra > 1024) muestra *= 2
                    val bitmap = resolver.openInputStream(uri).use {
                        BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply { inSampleSize = muestra })
                    } ?: error("Imagen inválida")
                    val carpeta = File(app.filesDir, "fotos").apply { mkdirs() }
                    val destino = File.createTempFile("perfil_", ".jpg", carpeta)
                    try {
                        destino.outputStream().use { check(bitmap.compress(Bitmap.CompressFormat.JPEG, 85, it)) }
                        app.usuarios.guardarFoto(destino.absolutePath)
                        val anterior = _estado.value.foto
                        if (anterior.isNotBlank()) {
                            val archivo = File(anterior)
                            if (archivo.parentFile?.canonicalPath == carpeta.canonicalPath) archivo.delete()
                        }
                        destino.absolutePath
                    } catch (e: Exception) {
                        destino.delete()
                        throw e
                    } finally { bitmap.recycle() }
                }
                _estado.update { it.copy(cargando = false, foto = ruta, aviso = "Foto actualizada") }
            } catch (e: CancellationException) { throw e
            } catch (_: Exception) {
                _estado.update { it.copy(cargando = false, mensaje = "No se pudo guardar la imagen. Prueba con otra foto.") }
            }
        }
    }
}
