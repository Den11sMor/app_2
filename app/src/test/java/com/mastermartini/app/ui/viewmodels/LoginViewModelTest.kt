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

class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val application: MasterMartiniApplication = mockk(relaxed = true)
    private val usuarioRepository: UsuarioRepository = mockk(relaxed = true)
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        every { application.usuarios } returns usuarioRepository
        viewModel = LoginViewModel(application)
    }

    @Test
    fun `al cambiar correo y clave se actualiza el estado de la UI`() {
        viewModel.correo("test@duoc.cl")
        viewModel.clave("Martini2026")

        val estado = viewModel.estado.value
        assertEquals("test@duoc.cl", estado.correo)
        assertEquals("Martini2026", estado.clave)
        assertNull(estado.errorCorreo)
        assertNull(estado.errorClave)
    }

    @Test
    fun `ingresar con campos vacios genera errores en el estado`() {
        viewModel.correo("")
        viewModel.clave("")

        viewModel.ingresar()

        val estado = viewModel.estado.value
        assertNotNull(estado.errorCorreo)
        assertNotNull(estado.errorClave)
        assertFalse(estado.exitoso)
    }

    @Test
    fun `ingresar con credenciales correctas actualiza estado a exitoso`() {
        viewModel.correo("deni@gmail.com")
        viewModel.clave("Martini2026")

        coEvery { usuarioRepository.ingresar("deni@gmail.com", "Martini2026") } returns true

        viewModel.ingresar()

        val estado = viewModel.estado.value
        assertTrue(estado.exitoso)
        assertFalse(estado.cargando)
        assertEquals("", estado.clave)
        coVerify { usuarioRepository.ingresar("deni@gmail.com", "Martini2026") }
    }

    @Test
    fun `ingresar con credenciales incorrectas muestra mensaje de error`() {
        viewModel.correo("deni@gmail.com")
        viewModel.clave("Incorrecta1")

        coEvery { usuarioRepository.ingresar("deni@gmail.com", "Incorrecta1") } returns false

        viewModel.ingresar()

        val estado = viewModel.estado.value
        assertFalse(estado.exitoso)
        assertFalse(estado.cargando)
        assertEquals("Correo o contraseña incorrectos", estado.mensaje)
    }
}
