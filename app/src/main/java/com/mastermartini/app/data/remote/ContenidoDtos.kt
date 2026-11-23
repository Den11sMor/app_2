package com.mastermartini.app.data.remote

data class ContenidoSolicitud(
    val titulo: String = "",
    val categoria: String = "",
    val duracion: String = "",
    val descripcion: String = "",
    val detalle: String = "",
    val videoUrl: String = "",
)

data class ContenidoRespuesta(
    val id: Long,
    val titulo: String,
    val categoria: String,
    val duracion: String,
    val descripcion: String,
    val detalle: String,
    val videoUrl: String = "",
) {
    fun paraEditar() =
        ContenidoSolicitud(titulo, categoria, duracion, descripcion, detalle, videoUrl)
}

data class ErrorServidor(val mensaje: String? = null, val errores: Map<String, String>? = null)
