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

class PerfilViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val application: MasterMartiniApplication = mockk(relaxed = true)
    private val usuarioRepository: UsuarioRepository = mockk(relaxed = true)
    private val usuarioFlow = MutableStateFlow<Usuario?>(null)
    private lateinit var viewModel: PerfilViewModel

    @Before
    fun setUp() {
        every { usuarioRepository.usuario } returns usuarioFlow
        every { application.usuarios } returns usuarioRepository
        viewModel = PerfilViewModel(application)
    }

    @Test
    fun `al recibir usuario actualiza el estado del perfil`() {
        val usuarioDummy = Usuario(1L, "Denis Morales", "deni@duoc.cl", "+56922224444", listOf("Gastronomía"), "")
        usuarioFlow.value = usuarioDummy

        val estado = viewModel.estado.value
        assertTrue(estado.listo)
        assertEquals("Denis Morales", estado.nombre)
        assertEquals("deni@duoc.cl", estado.correo)
        assertEquals("+56922224444", estado.telefono)
    }

    @Test
    fun `guardar con datos invalidos genera mensajes de error`() {
        usuarioFlow.value = Usuario(1L, "Denis", "deni@duoc.cl", "+56922224444", emptyList(), "")
        viewModel.nombre("A") // Nombre inválido (< 3 caracteres)
        viewModel.telefono("abc") // Teléfono inválido

        viewModel.guardar()

        val estado = viewModel.estado.value
        assertNotNull(estado.errorNombre)
        assertNotNull(estado.errorTelefono)
    }

    @Test
    fun `guardar con datos validos invoca repositorio`() {
        usuarioFlow.value = Usuario(1L, "Denis Morales", "deni@duoc.cl", "+56922224444", emptyList(), "")
        viewModel.nombre("Denis Morales")
        viewModel.telefono("+56922224444")

        coEvery { usuarioRepository.guardarPerfil("Denis Morales", "+56922224444") } returns Unit

        viewModel.guardar()

        val estado = viewModel.estado.value
        assertEquals("Perfil actualizado", estado.aviso)
        assertFalse(estado.cargando)
        coVerify { usuarioRepository.guardarPerfil("Denis Morales", "+56922224444") }
    }
}
