package com.mastermartini.app.domain.model

class Curso(id: Int, titulo: String, categoria: String, duracion: String, descripcion: String, detalle: String) :
    Contenido(id, titulo, categoria, duracion, descripcion, detalle) {
    override val tipo = "Curso"
    override fun accion() = "Ver curso"
}
