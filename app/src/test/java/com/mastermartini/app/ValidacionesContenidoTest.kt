package com.mastermartini.app

import com.mastermartini.app.data.remote.ContenidoSolicitud
import com.mastermartini.app.utils.ValidacionesContenido
import org.junit.Assert.*
import org.junit.Test

class ValidacionesContenidoTest {
    private val correcto =
        ContenidoSolicitud(
            "Torta",
            "Pastelería",
            "30 minutos",
            "Descripción",
            "Ingredientes y pasos",
        )

    @Test
    fun obligatoriosLongitudesYUrlSegura() {
        assertEquals(5, ValidacionesContenido.validar(ContenidoSolicitud(), false).size)
        assertTrue(ValidacionesContenido.validar(correcto, false).isEmpty())
        assertEquals(1, ValidacionesContenido.validar(correcto, true).size)
        assertTrue(
            ValidacionesContenido.validar(
                    correcto.copy(videoUrl = "https://example.com/video.mp4"),
                    true,
                )
                .isEmpty()
        )
        assertTrue(
            ValidacionesContenido.validar(correcto.copy(titulo = "a".repeat(161)), false)
                .containsKey("titulo")
        )
        listOf(
                "javascript:alert(1)",
                "http://example.com/video.mp4",
                "https://",
                "https://user:pass@example.com/x",
                "https://example.com/a b",
            )
            .forEach {
                assertTrue(
                    ValidacionesContenido.validar(correcto.copy(videoUrl = it), true)
                        .containsKey("videoUrl")
                )
            }
    }

    @Test
    fun baseUrlValidaHttpsOLoopbackSinCredenciales() {
        assertEquals(
            "https://example.com/",
            ValidacionesContenido.servidor(" https://example.com "),
        )
        listOf("http://127.0.0.1:8080/", "http://localhost:8080/", "http://10.0.2.2:8080/")
            .forEach { assertEquals(it, ValidacionesContenido.servidor(it)) }
        listOf(
                "http://example.com/",
                "ftp://example.com/",
                "https://user:pass@example.com/",
                "cualquier cosa",
                "https://example.com/?key=secret",
            )
            .forEach {
                try {
                    ValidacionesContenido.servidor(it)
                    fail(it)
                } catch (_: IllegalArgumentException) {}
            }
    }
}
