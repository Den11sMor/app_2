package com.mastermartini.app.data.remote.api

import com.mastermartini.app.data.remote.dto.LoginRequest
import com.mastermartini.app.data.remote.dto.LoginResponse
import com.mastermartini.app.data.remote.dto.RegistroRequest
import com.mastermartini.app.data.remote.dto.UsuarioDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// Interface de Retrofit con los endpoints exactos que coinciden con UsuarioController en Spring Boot.
interface UsuarioApi {

    // Endpoint de Inicio de Sesión en Spring Boot (/api/usuarios/login)
    @POST("api/usuarios/login")
    suspend fun iniciarSesion(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    // Endpoint de Registro de Usuario en Spring Boot (/api/usuarios/registro)
    @POST("api/usuarios/registro")
    suspend fun registrarUsuario(
        @Body request: RegistroRequest
    ): Response<UsuarioDto>

    // Endpoint para listar todos los usuarios
    @GET("api/usuarios")
    suspend fun listarUsuarios(): Response<List<UsuarioDto>>
}
