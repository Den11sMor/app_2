package com.mastermartini.app.domain.model

class Receta(id: Int, titulo: String, categoria: String, duracion: String, descripcion: String, detalle: String) :
    Contenido(id, titulo, categoria, duracion, descripcion, detalle) {
    override val tipo = "Receta"
    override fun accion() = "Ver receta"
}
