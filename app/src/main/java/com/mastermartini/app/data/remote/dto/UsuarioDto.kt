package com.mastermartini.app.data.remote.dto

import com.google.gson.annotations.SerializedName

// DTO para enviar la solicitud de inicio de sesión a Spring Boot.
data class LoginRequest(val correo: String, val password: String)

// DTO que representa los datos públicos de un usuario en Spring Boot.
data class UsuarioDto(
    @SerializedName("idUsuario") val idUsuario: Long = 0,
    val nombre: String = "",
    val correo: String = "",
    val telefono: String? = null,
    val foto: String? = null,
)

// DTO para recibir la respuesta del inicio de sesión desde Spring Boot (incluye el token JWT).
data class LoginResponse(
    val token: String? = null,
    val tipo: String? = null,
    val usuario: UsuarioDto? = null,
)

// DTO para enviar la solicitud de registro de un nuevo usuario a Spring Boot.
data class RegistroRequest(
    val nombre: String,
    val correo: String,
    val telefono: String,
    val password: String,
    val foto: String? = null,
    val intereses: List<String> = emptyList(),
)
