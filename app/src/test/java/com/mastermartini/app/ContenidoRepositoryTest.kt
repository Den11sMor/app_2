package com.mastermartini.app

import com.mastermartini.app.data.remote.*
import com.mastermartini.app.data.repository.*
import java.net.SocketTimeoutException
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.*
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@RunWith(Parameterized::class)
class ContenidoRepositoryTest(private val tipo: String, private val ruta: String) {
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun tipos() =
            listOf(
                arrayOf("Curso", "cursos"),
                arrayOf("Receta", "recetas"),
                arrayOf("Video", "videos"),
            )
    }

    private lateinit var server: MockWebServer
    private lateinit var repo: CatalogoRemoto
    private val registro =
        """{"id":8,"titulo":"Torta","categoria":"Pastelería","duracion":"30 min","descripcion":"Ejemplo","detalle":"Pasos","videoUrl":"https://example.com/v.mp4"}"""
    private val datos =
        ContenidoSolicitud(
            "Torta",
            "Pastelería",
            "30 min",
            "Ejemplo",
            "Pasos",
            "https://example.com/v.mp4",
        )

    @Before
    fun preparar() {
        server = MockWebServer()
        server.start()
        val retrofit =
            Retrofit.Builder()
                .baseUrl(server.url("/"))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        repo =
            when (tipo) {
                "Curso" -> CursoRepository(retrofit.create(CursoApi::class.java))
                "Video" -> VideoRepository(retrofit.create(VideoApi::class.java))
                else -> RecetaRepository(retrofit.create(RecetaApi::class.java))
            }
    }

    @After
    fun terminar() {
        server.shutdown()
    }

    private fun responder(body: String, code: Int = 200) {
        server.enqueue(
            MockResponse()
                .setResponseCode(code)
                .setHeader("Content-Type", "application/json")
                .setBody(body)
        )
    }

    @Test
    fun contratoHttpCompleto() = runTest {
        responder("[$registro]")
        assertEquals(8L, repo.listar().single().id)
        assertEquals("/api/$ruta", server.takeRequest().path)
        responder(registro)
        assertEquals("Torta", repo.consultar(8).titulo)
        assertEquals("GET", server.takeRequest().method)
        responder(registro, 201)
        repo.crear(datos)
        val post = server.takeRequest()
        assertEquals("POST", post.method)
        assertEquals("/api/$ruta", post.path)
        assertTrue(post.body.readUtf8().contains("videoUrl"))
        responder(registro)
        repo.actualizar(8, datos)
        val put = server.takeRequest()
        assertEquals("PUT", put.method)
        assertEquals("/api/$ruta/8", put.path)
        server.enqueue(MockResponse().setResponseCode(204))
        repo.eliminar(8)
        val delete = server.takeRequest()
        assertEquals("DELETE", delete.method)
        assertEquals("/api/$ruta/8", delete.path)
    }

    @Test
    fun traduceErrorDeValidacionYErroresHttp() = runTest {
        responder("""{"mensaje":"Revisa los campos","errores":{"titulo":"Obligatorio"}}""", 400)
        try {
            repo.crear(datos)
            fail("Debía fallar")
        } catch (e: ErrorRemoto) {
            assertEquals("Obligatorio", e.campos["titulo"])
        }
        for (code in listOf(401, 404, 500)) {
            responder("no es json", code)
            try {
                repo.listar()
                fail("Debía fallar")
            } catch (e: ErrorRemoto) {
                assertTrue(e.message!!.isNotBlank())
            }
        }
    }

    @Test
    fun traduceErroresDeRedSinExponerDetalles() = runTest {
        val api =
            object : ContenidoApi {
                override suspend fun listar(): List<ContenidoRespuesta> =
                    throw SocketTimeoutException("detalle interno")

                override suspend fun consultar(id: Long) = error("No usado")

                override suspend fun crear(datos: ContenidoSolicitud) = error("No usado")

                override suspend fun actualizar(id: Long, datos: ContenidoSolicitud) =
                    error("No usado")

                override suspend fun eliminar(id: Long) {
                    error("No usado")
                }
            }
        try {
            ContenidoRemotoRepository(api).listar()
            fail("Debía fallar")
        } catch (e: ErrorRemoto) {
            assertFalse(e.message!!.contains("detalle interno"))
        }
    }
}
