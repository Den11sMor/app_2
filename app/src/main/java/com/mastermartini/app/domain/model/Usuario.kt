package com.mastermartini.app.domain.model

data class Usuario (
    val id: Int = 0,
    val nombre : String,
    val correo: String,
    val telefono: String,
    val tipoNegocio: String,
    val tamanoNegocio: String
)