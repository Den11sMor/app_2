package com.mastermartini.app.utils

import com.mastermartini.app.data.remote.ContenidoSolicitud
import java.net.URI

object ValidacionesContenido {
    fun validar(datos: ContenidoSolicitud, esVideo: Boolean): Map<String, String> {
        val errores = linkedMapOf<String, String>()
        listOf(
                Triple("titulo", datos.titulo, 160),
                Triple("categoria", datos.categoria, 100),
                Triple("duracion", datos.duracion, 80),
                Triple("descripcion", datos.descripcion, 1000),
                Triple("detalle", datos.detalle, 16000),
            )
            .forEach { (campo, valor, maximo) ->
                if (valor.isBlank()) errores[campo] = "Campo obligatorio"
                else if (valor.length > maximo) errores[campo] = "Usa hasta $maximo caracteres"
            }
        if (esVideo && datos.videoUrl.isBlank())
            errores["videoUrl"] = "Ingresa el enlace HTTPS directo al video MP4"
        if (
            datos.videoUrl.isNotBlank() &&
                (datos.videoUrl.length > 2000 || !esHttps(datos.videoUrl))
        )
            errores["videoUrl"] = "Usa una dirección HTTPS válida"
        return errores
    }

    private fun esHttps(valor: String) =
        runCatching {
                val uri = URI(valor)
                uri.scheme == "https" && !uri.host.isNullOrBlank() && uri.userInfo == null
            }
            .getOrDefault(false)

    fun servidor(valor: String): String {
        val uri =
            runCatching { URI(valor.trim()) }
                .getOrElse { throw IllegalArgumentException("Dirección de servidor inválida") }
        require(
            !uri.host.isNullOrBlank() &&
                uri.userInfo == null &&
                uri.query == null &&
                uri.fragment == null
        ) {
            "Ingresa una URL de servidor válida, sin credenciales"
        }
        require(
            uri.scheme == "https" ||
                (uri.scheme == "http" && uri.host in listOf("localhost", "127.0.0.1", "10.0.2.2"))
        ) {
            "El servidor remoto debe usar HTTPS. Para USB usa http://127.0.0.1:8080/"
        }
        return valor.trim().trimEnd('/') + "/"
    }
}
