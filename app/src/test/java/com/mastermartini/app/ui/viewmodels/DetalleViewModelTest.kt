package com.mastermartini.app.ui.viewmodels

import android.os.SystemClock
import androidx.lifecycle.SavedStateHandle
import com.mastermartini.app.MainDispatcherRule
import com.mastermartini.app.MasterMartiniApplication
import com.mastermartini.app.data.repository.ContenidoRepository
import com.mastermartini.app.data.repository.FavoritoRepository
import com.mastermartini.app.data.repository.UsuarioRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DetalleViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val application: MasterMartiniApplication = mockk(relaxed = true)
    private val contenidoRepository: ContenidoRepository = mockk(relaxed = true)
    private val favoritoRepository: FavoritoRepository = mockk(relaxed = true)
    private val usuarioRepository: UsuarioRepository = mockk(relaxed = true)
    private val savedStateHandle = SavedStateHandle()

    private lateinit var viewModel: DetalleViewModel

    @Before
    fun setUp() {
        mockkStatic(SystemClock::class)
        every { SystemClock.elapsedRealtime() } returns 100000L

        every { usuarioRepository.usuario } returns MutableStateFlow(null)
        every { contenidoRepository.contenidos } returns MutableStateFlow(emptyList())
        every { application.usuarios } returns usuarioRepository
        every { application.contenidos } returns contenidoRepository
        every { application.favoritos } returns favoritoRepository

        viewModel = DetalleViewModel(application, savedStateHandle)
    }

    @After
    fun tearDown() {
        unmockkStatic(SystemClock::class)
    }

    @Test
    fun `iniciar temporizador establece contador en 15 minutos`() {
        viewModel.iniciar()

        assertTrue(viewModel.segundos.value > 0)
        assertFalse(viewModel.terminado.value)
    }

    @Test
    fun `cancelar temporizador resetea el contador a cero`() {
        viewModel.iniciar()
        viewModel.cancelar()

        assertEquals(0, viewModel.segundos.value)
        assertFalse(viewModel.terminado.value)
    }
}
