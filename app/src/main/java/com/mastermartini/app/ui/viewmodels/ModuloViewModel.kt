package com.mastermartini.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mastermartini.app.data.remote.*
import com.mastermartini.app.data.repository.*
import com.mastermartini.app.utils.ValidacionesContenido
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// Estado completo de la pantalla: listado, selección, formulario, errores y carga.
// Compose observa este estado y actualiza la vista cuando cambia alguno de sus valores.
data class ModuloUiState(
    val lista: List<ContenidoRespuesta> = emptyList(),
    val seleccionado: ContenidoRespuesta? = null,
    val formulario: ContenidoSolicitud? = null,
    val errores: Map<String, String> = emptyMap(),
    val ocupado: Boolean = false,
    val mensaje: String? = null,
    val exito: String? = null,
)

open class ModuloViewModel(private val repository: CatalogoRemoto, private val tipo: String) :
    ViewModel() {
    // Solo el ViewModel modifica el MutableStateFlow. La UI recibe una versión de solo lectura.
    private val _estado = MutableStateFlow(ModuloUiState())
    val estado = _estado.asStateFlow()

    init {
        cargar()
    }

    // GET del listado. Primero se espera la respuesta y después se actualiza el estado de la UI.
    fun cargar() = operar {
        val lista = repository.listar()
        _estado.update { it.copy(lista = lista) }
    }

    // GET por ID: consulta de nuevo al servidor, no supone que la tarjeta tiene el último dato.
    fun consultar(id: Long) = operar {
        val item = repository.consultar(id)
        _estado.update { it.copy(seleccionado = item) }
    }

    // Un formulario vacío, sin selección, indica que se creará un registro nuevo.
    fun nuevo() {
        if (!_estado.value.ocupado)
            _estado.update {
                it.copy(
                    formulario = ContenidoSolicitud(),
                    seleccionado = null,
                    errores = emptyMap(),
                    mensaje = null,
                    exito = null,
                )
            }
    }

    fun editar() {
        _estado.update {
            it.copy(
                formulario = it.seleccionado?.paraEditar(),
                errores = emptyMap(),
                mensaje = null,
                exito = null,
            )
        }
    }

    fun cerrar() {
        if (!_estado.value.ocupado)
            _estado.update {
                it.copy(
                    seleccionado = null,
                    formulario = null,
                    errores = emptyMap(),
                    mensaje = null,
                )
            }
    }

    // Al corregir un campo se limpia solo su error; los demás errores siguen visibles.
    fun cambiar(campo: String, valor: String) {
        _estado.update { estado ->
            val f = estado.formulario ?: return@update estado
            val siguiente =
                when (campo) {
                    "titulo" -> f.copy(titulo = valor)
                    "categoria" -> f.copy(categoria = valor)
                    "duracion" -> f.copy(duracion = valor)
                    "descripcion" -> f.copy(descripcion = valor)
                    "detalle" -> f.copy(detalle = valor)
                    "videoUrl" -> f.copy(videoUrl = valor)
                    else -> f
                }
            estado.copy(formulario = siguiente, errores = estado.errores - campo, mensaje = null)
        }
    }

    // Valida antes de enviar. Sin selección usa POST; con un ID seleccionado usa PUT.
    fun guardar() {
        val actual = _estado.value
        val datos = actual.formulario ?: return
        if (actual.ocupado) return
        val errores = ValidacionesContenido.validar(datos, tipo == "Video")
        _estado.update { it.copy(errores = errores) }
        if (errores.isNotEmpty()) return
        operar {
            val resultado =
                if (actual.seleccionado == null) repository.crear(datos)
                else repository.actualizar(actual.seleccionado.id, datos)
            // La escritura ya fue confirmada: un fallo al refrescar no debe repetir el POST.
            _estado.update {
                it.copy(
                    formulario = null,
                    seleccionado = resultado,
                    lista =
                        (it.lista.filterNot { fila -> fila.id == resultado.id } + resultado)
                            .sortedBy { fila -> fila.id },
                    exito = "Guardado en el servidor",
                )
            }
        }
    }

    // La pantalla pide confirmación. Aquí se elimina la fila solo si el servidor confirmó el
    // DELETE.
    fun eliminar() {
        val actual = _estado.value.seleccionado ?: return
        operar {
            repository.eliminar(actual.id)
            _estado.update {
                it.copy(
                    seleccionado = null,
                    lista = it.lista.filterNot { fila -> fila.id == actual.id },
                    exito = "Eliminado del servidor",
                )
            }
        }
    }

    // Ejecuta en viewModelScope y bloquea pulsaciones repetidas mientras hay una solicitud en
    // curso.
    // finally quita el indicador de carga aunque falle la red. La cancelación se vuelve a lanzar.
    private fun operar(accion: suspend () -> Unit) {
        if (_estado.value.ocupado) return
        _estado.update { it.copy(ocupado = true, mensaje = null, exito = null) }
        viewModelScope.launch {
            try {
                accion()
            } catch (e: CancellationException) {
                throw e
            } catch (e: ErrorRemoto) {
                _estado.update { it.copy(mensaje = e.message, errores = e.campos) }
            } catch (e: Exception) {
                _estado.update {
                    it.copy(mensaje = "No se pudo completar la operación. Intenta nuevamente.")
                }
            } finally {
                _estado.update { it.copy(ocupado = false) }
            }
        }
    }
}
