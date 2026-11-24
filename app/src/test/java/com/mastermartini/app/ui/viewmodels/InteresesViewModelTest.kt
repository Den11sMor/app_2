package com.mastermartini.app.ui.viewmodels

import com.mastermartini.app.MainDispatcherRule
import com.mastermartini.app.MasterMartiniApplication
import com.mastermartini.app.data.repository.UsuarioRepository
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

class InteresesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val application: MasterMartiniApplication = mockk(relaxed = true)
    private val usuarioRepository: UsuarioRepository = mockk(relaxed = true)
    private val usuarioFlow = MutableStateFlow<Usuario?>(null)
    private lateinit var viewModel: InteresesViewModel

    @Before
    fun setUp() {
        every { usuarioRepository.usuario } returns usuarioFlow
        every { application.usuarios } returns usuarioRepository
        viewModel = InteresesViewModel(application)
    }

    @Test
    fun `seleccionar agrega y quita temas de interes`() {
        viewModel.seleccionar("Cocina")
        assertTrue("Cocina" in viewModel.estado.value.seleccionados)

        viewModel.seleccionar("Cocina")
        assertFalse("Cocina" in viewModel.estado.value.seleccionados)
    }

    @Test
    fun `guardar sin seleccion muestra mensaje de advertencia`() {
        usuarioFlow.value = Usuario(1L, "Denis", "deni@duoc.cl", "+56922224444", emptyList(), "")
        viewModel.guardar()

        val estado = viewModel.estado.value
        assertEquals("Selecciona al menos un interés", estado.mensaje)
    }

    @Test
    fun `guardar con intereses seleccionados actualiza repositorio`() {
        usuarioFlow.value = Usuario(1L, "Denis", "deni@duoc.cl", "+56922224444", emptyList(), "")
        viewModel.seleccionar("Cocina")
        viewModel.seleccionar("Gastronomía")

        coEvery { usuarioRepository.guardarIntereses(setOf("Cocina", "Gastronomía")) } returns Unit

        viewModel.guardar()

        val estado = viewModel.estado.value
        assertTrue(estado.guardado)
        coVerify { usuarioRepository.guardarIntereses(setOf("Cocina", "Gastronomía")) }
    }
}
