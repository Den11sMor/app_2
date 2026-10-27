package com.mastermartini.app.domain.model

// La interfaz no necesita conocer el hash de la contraseña.
data class Usuario(
    val id: Long,
    val nombre: String,
    val correo: String,
    val telefono: String,
    val intereses: List<String>,
    val foto: String
)
