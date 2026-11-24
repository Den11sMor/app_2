package com.mastermartini.app.ui.viewmodels

import com.mastermartini.app.MainDispatcherRule
import com.mastermartini.app.MasterMartiniApplication
import com.mastermartini.app.data.repository.ContenidoRepository
import com.mastermartini.app.data.repository.FavoritoRepository
import com.mastermartini.app.data.repository.UsuarioRepository
import com.mastermartini.app.domain.model.Receta
import com.mastermartini.app.domain.model.Usuario
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ContenidoViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val application: MasterMartiniApplication = mockk(relaxed = true)
    private val contenidoRepository: ContenidoRepository = mockk(relaxed = true)
    private val favoritoRepository: FavoritoRepository = mockk(relaxed = true)
    private val usuarioRepository: UsuarioRepository = mockk(relaxed = true)

    private val usuarioFlow = MutableStateFlow<Usuario?>(null)
    private val contenidosFlow = MutableStateFlow(listOf(Receta(1, "Torta de chocolate", "Pastelería", "45 min", "Desc", "Detalle")))
    private val favoritosFlow = MutableStateFlow(listOf(1))

    private lateinit var viewModel: ContenidoViewModel

    @Before
    fun setUp() {
        every { usuarioRepository.usuario } returns usuarioFlow
        every { usuarioRepository.idActual } returns 1L
        every { contenidoRepository.contenidos } returns contenidosFlow
        every { favoritoRepository.observarIds(1L) } returns favoritosFlow

        every { application.usuarios } returns usuarioRepository
        every { application.contenidos } returns contenidoRepository
        every { application.favoritos } returns favoritoRepository

        viewModel = ContenidoViewModel(application)
    }

    @Test
    fun `buscar actualiza la consulta de busqueda`() {
        viewModel.buscar("chocolate")
        assertEquals("chocolate", viewModel.consulta.value)
    }

    @Test
    fun `cambiarFavorito invoca la operacion del repositorio`() {
        coEvery { favoritoRepository.cambiar(1L, 1, true) } returns Unit

        viewModel.cambiarFavorito(1)

        coVerify { favoritoRepository.cambiar(1L, 1, false) }
    }
}
