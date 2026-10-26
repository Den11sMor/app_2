package com.mastermartini.app.domain.model

class Video(id: Int, titulo: String, categoria: String, duracion: String, descripcion: String, detalle: String) :
    Contenido(id, titulo, categoria, duracion, descripcion, detalle) {
    override val tipo = "Video"
    override fun accion() = "Ver guía del video"
}
