package com.mastermartini.app.data.remote

// Datos que se envían al crear o editar. El ID no se pide al usuario porque lo genera MySQL.
data class ContenidoSolicitud(
    val titulo: String = "",
    val categoria: String = "",
    val duracion: String = "",
    val descripcion: String = "",
    val detalle: String = "",
    val videoUrl: String = "",
)

// La respuesta sí incluye el ID. Se utiliza para consultar, editar o eliminar el mismo registro.
data class ContenidoRespuesta(
    val id: Long,
    val titulo: String,
    val categoria: String,
    val duracion: String,
    val descripcion: String,
    val detalle: String,
    val videoUrl: String = "",
) {
    // Copia los datos recibidos al formulario, sin incluir el identificador en el cuerpo del PUT.
    fun paraEditar() =
        ContenidoSolicitud(titulo, categoria, duracion, descripcion, detalle, videoUrl)
}

data class ErrorServidor(val mensaje: String? = null, val errores: Map<String, String>? = null)
