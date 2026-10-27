package com.mastermartini.app.domain.model

// Las tarjetas trabajan con Contenido sin depender de un tipo específico.
abstract class Contenido(
    val id: Int,
    val titulo: String,
    val categoria: String,
    val duracion: String,
    val descripcion: String,
    val detalle: String
) {
    abstract val tipo: String
    abstract fun accion(): String
}
