package com.mastermartini.app.ui.viewmodels

import com.mastermartini.app.MainDispatcherRule
import com.mastermartini.app.MasterMartiniApplication
import com.mastermartini.app.data.repository.UsuarioRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RegistroViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val application: MasterMartiniApplication = mockk(relaxed = true)
    private val usuarioRepository: UsuarioRepository = mockk(relaxed = true)
    private lateinit var viewModel: RegistroViewModel

    @Before
    fun setUp() {
        every { application.usuarios } returns usuarioRepository
        viewModel = RegistroViewModel(application)
    }

    @Test
    fun `cambiar actualiza los campos correspondientes del formulario`() {
        viewModel.cambiar("nombre", "Denis Morales")
        viewModel.cambiar("correo", "deni@duoc.cl")
        viewModel.cambiar("telefono", "+56922224444")
        viewModel.cambiar("clave", "Martini2026")
        viewModel.cambiar("repetida", "Martini2026")

        val estado = viewModel.estado.value
        assertEquals("Denis Morales", estado.nombre)
        assertEquals("deni@duoc.cl", estado.correo)
        assertEquals("+56922224444", estado.telefono)
        assertEquals("Martini2026", estado.clave)
        assertEquals("Martini2026", estado.repetida)
    }

    @Test
    fun `registrar con campos invalidos genera mapa de errores`() {
        viewModel.cambiar("nombre", "A")
        viewModel.cambiar("correo", "correo_invalido")

        viewModel.registrar()

        val estado = viewModel.estado.value
        assertFalse(estado.exitoso)
        assertTrue(estado.errores.isNotEmpty())
    }

    @Test
    fun `registrar con datos correctos invoca repositorio y completa exitosamente`() {
        viewModel.cambiar("nombre", "Denis Morales")
        viewModel.cambiar("correo", "deni@duoc.cl")
        viewModel.cambiar("telefono", "+56922224444")
        viewModel.cambiar("clave", "Martini2026")
        viewModel.cambiar("repetida", "Martini2026")

        coEvery {
            usuarioRepository.registrar("Denis Morales", "deni@duoc.cl", "+56922224444", "Martini2026")
        } returns null

        viewModel.registrar()

        val estado = viewModel.estado.value
        assertTrue(estado.exitoso)
        assertFalse(estado.cargando)
        assertEquals("", estado.clave)
        assertEquals("", estado.repetida)
        coVerify { usuarioRepository.registrar("Denis Morales", "deni@duoc.cl", "+56922224444", "Martini2026") }
    }

    @Test
    fun `registrar cuando el correo ya existe en el servidor guarda el error`() {
        viewModel.cambiar("nombre", "Denis Morales")
        viewModel.cambiar("correo", "deni@duoc.cl")
        viewModel.cambiar("telefono", "+56922224444")
        viewModel.cambiar("clave", "Martini2026")
        viewModel.cambiar("repetida", "Martini2026")

        coEvery {
            usuarioRepository.registrar("Denis Morales", "deni@duoc.cl", "+56922224444", "Martini2026")
        } returns "Ese correo ya está registrado"

        viewModel.registrar()

        val estado = viewModel.estado.value
        assertFalse(estado.exitoso)
        assertEquals("Ese correo ya está registrado", estado.mensaje)
    }
}
