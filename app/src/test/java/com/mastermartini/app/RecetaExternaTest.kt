package com.mastermartini.app

import com.mastermartini.app.data.remote.*
import com.mastermartini.app.data.repository.*
import com.mastermartini.app.ui.viewmodels.RecetaExternaViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import okhttp3.mockwebserver.*
import org.junit.*
import org.junit.Assert.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@OptIn(ExperimentalCoroutinesApi::class)
class RecetaExternaTest {
    @Before
    fun preparar() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @After
    fun terminar() {
        Dispatchers.resetMain()
    }

    @Test
    fun interpretaRespuestaYDetallesDeTheMealDb() = runTest {
        val server = MockWebServer()
        server.start()
        try {
            val api =
                Retrofit.Builder()
                    .baseUrl(server.url("/"))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(RecetaExternaApi::class.java)
            val repo = RecetaExternaRepository(api)
            val body =
                """{"meals":[{"idMeal":"53000","strMeal":"Cake","strCategory":"Dessert","strArea":"British","strInstructions":"Bake","strMealThumb":"https://example.com/cake.jpg"}]}"""
            server.enqueue(MockResponse().setBody(body))
            assertEquals("Cake", repo.buscar(" cake ").single().strMeal)
            assertEquals("/search.php?s=cake", server.takeRequest().path)
            server.enqueue(MockResponse().setBody(body))
            assertEquals("Bake", repo.consultar("53000")!!.strInstructions)
            assertEquals("/lookup.php?i=53000", server.takeRequest().path)
            server.enqueue(MockResponse().setBody("""{"meals":null}"""))
            assertTrue(repo.buscar("inexistente").isEmpty())
            server.enqueue(MockResponse().setBody("""{"meals":null}"""))
            assertNull(repo.consultar("0"))
        } finally {
            server.shutdown()
        }
    }

    @Test
    fun viewModelMuestraListaDetalleErroresYRecuperacion() = runTest {
        val receta = RecetaExterna("1", "Cake", "Dessert", "British", "Bake", null, null)
        var fallar = false
        val repo =
            object : RecetasExternas {
                override suspend fun buscar(nombre: String): List<RecetaExterna> {
                    if (fallar) error("Sin red")
                    return listOf(receta)
                }

                override suspend fun consultar(id: String): RecetaExterna? =
                    if (id == "1") receta else null
            }
        val vm = RecetaExternaViewModel(repo)
        advanceUntilIdle()
        assertEquals(1, vm.estado.value.lista.size)
        vm.consultar("1")
        advanceUntilIdle()
        assertEquals("Cake", vm.estado.value.detalle!!.strMeal)
        vm.cerrarDetalle()
        assertNull(vm.estado.value.detalle)
        vm.consultar("0")
        advanceUntilIdle()
        assertNotNull(vm.estado.value.error)
        fallar = true
        vm.buscar("cake")
        advanceUntilIdle()
        assertNotNull(vm.estado.value.error)
        assertFalse(vm.estado.value.cargando)
        fallar = false
        vm.buscar("cake")
        advanceUntilIdle()
        assertNull(vm.estado.value.error)
    }
}
