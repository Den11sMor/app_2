package com.mastermartini.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mastermartini.app.data.remote.RecetaExterna
import com.mastermartini.app.data.repository.RecetasExternas
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class RecetaExternaUiState(
    val lista: List<RecetaExterna> = emptyList(),
    val detalle: RecetaExterna? = null,
    val cargando: Boolean = false,
    val error: String? = null,
)

class RecetaExternaViewModel(private val repository: RecetasExternas) : ViewModel() {
    private val _estado = MutableStateFlow(RecetaExternaUiState())
    val estado = _estado.asStateFlow()
    private var trabajo: Job? = null
    private var generacion = 0

    init {
        buscar("cake")
    }

    fun buscar(nombre: String) {
        cargar {
            val lista = repository.buscar(nombre)
            _estado.update { it.copy(lista = lista, detalle = null) }
        }
    }

    fun consultar(id: String) {
        cargar {
            val detalle = repository.consultar(id)
            _estado.update {
                it.copy(
                    detalle = detalle,
                    error = if (detalle == null) "La receta ya no está disponible" else null,
                )
            }
        }
    }

    fun cerrarDetalle() {
        generacion++
        trabajo?.cancel()
        _estado.update { it.copy(detalle = null, cargando = false) }
    }

    // Cancela la búsqueda anterior para que una respuesta atrasada no reemplace la búsqueda actual.
    private fun cargar(accion: suspend () -> Unit) {
        val actual = ++generacion
        trabajo?.cancel()
        trabajo =
            viewModelScope.launch {
                _estado.update { it.copy(cargando = true, error = null) }
                try {
                    accion()
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    _estado.update {
                        it.copy(
                            error =
                                "No se pudo consultar TheMealDB. Revisa internet e intenta nuevamente."
                        )
                    }
                } finally {
                    if (generacion == actual) _estado.update { it.copy(cargando = false) }
                }
            }
    }
}
