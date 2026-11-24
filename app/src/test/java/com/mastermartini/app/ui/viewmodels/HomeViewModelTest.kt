package com.mastermartini.app.ui.viewmodels

import com.mastermartini.app.MainDispatcherRule
import com.mastermartini.app.MasterMartiniApplication
import com.mastermartini.app.data.repository.UsuarioRepository
import com.mastermartini.app.domain.model.Usuario
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val application: MasterMartiniApplication = mockk(relaxed = true)
    private val usuarioRepository: UsuarioRepository = mockk(relaxed = true)
    private val usuarioFlow = MutableStateFlow<Usuario?>(null)
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        every { usuarioRepository.idActual } returns 10L
        every { usuarioRepository.usuario } returns usuarioFlow
        every { application.usuarios } returns usuarioRepository
        viewModel = HomeViewModel(application)
    }

    @Test
    fun `idInicial obtiene la sesion activa del repositorio`() {
        assertEquals(10L, viewModel.idInicial)
    }

    @Test
    fun `cerrarSesion invoca metodo del repositorio`() {
        viewModel.cerrarSesion()
        verify { usuarioRepository.cerrarSesion() }
    }
}
