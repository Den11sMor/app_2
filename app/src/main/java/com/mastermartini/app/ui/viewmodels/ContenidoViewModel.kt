package com.mastermartini.app.ui.viewmodels

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.mastermartini.app.domain.model.Contenido
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ContenidoUiState(
    val contenidos: List<Contenido> = emptyList(),
    val favoritos: List<Int> = emptyList(),
    val cargando: Boolean = true,
    val mensaje: String? = null,
    val guardando: Boolean = false
)

open class ContenidoViewModel(application: Application) : BaseViewModel(application) {
    private val _estado = MutableStateFlow(ContenidoUiState())
    val estado = _estado.asStateFlow()
    private var carga: kotlinx.coroutines.Job? = null
    private val busqueda = MutableStateFlow("")
    val consulta = busqueda.asStateFlow()

    init { cargar() }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun cargar() {
        carga?.cancel()
        _estado.update { it.copy(cargando = true, mensaje = null) }
        carga = viewModelScope.launch {
            try {
                val ids = app.usuarios.usuario.flatMapLatest { usuario ->
                    if (usuario == null) flowOf(emptyList()) else app.favoritos.observarIds(usuario.id)
                }
                combine(app.contenidos.contenidos, ids) { contenidos, favoritos ->
                    contenidos to favoritos
                }.collect { (contenidos, favoritos) ->
                    _estado.update { it.copy(contenidos = contenidos, favoritos = favoritos, cargando = false) }
                }
            } catch (e: CancellationException) { throw e
            } catch (_: Exception) {
                _estado.update { it.copy(cargando = false, mensaje = "No se pudo cargar el catálogo. Vuelve a intentar.") }
            }
        }
    }

    fun buscar(valor: String) { busqueda.value = valor }

    fun cambiarFavorito(id: Int) {
        if (_estado.value.guardando) return
        val guardado = id in _estado.value.favoritos
        _estado.update { it.copy(guardando = true, mensaje = null) }
        viewModelScope.launch {
            try {
                app.favoritos.cambiar(app.usuarios.idActual, id, guardado)
            } catch (e: CancellationException) { throw e
            } catch (_: Exception) {
                _estado.update { it.copy(mensaje = "No se pudo actualizar el favorito") }
            } finally {
                _estado.update { it.copy(guardando = false) }
            }
        }
    }
}
