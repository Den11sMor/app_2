package com.mastermartini.app.data.repository

import com.google.gson.Gson
import com.mastermartini.app.data.remote.ErrorServidor
import com.mastermartini.app.data.remote.api.UsuarioApi
import com.mastermartini.app.data.remote.dto.*
import java.io.IOException
import kotlinx.coroutines.CancellationException
import retrofit2.Response

// Usa los DTOs y endpoints del compañero. Un rechazo del servidor no se convierte en login local.
class UsuarioRemotoRepository(
    private val api: UsuarioApi,
    private val guardarToken: (String) -> Unit = {},
) {
    suspend fun registrar(datos: RegistroRequest): UsuarioDto = ejecutar {
        val respuesta = api.registrarUsuario(datos)
        if (!respuesta.isSuccessful) throw error(respuesta)
        validar(respuesta.body(), datos.correo)
        // El registro termina con sesión remota iniciada para poder abrir los contenidos
        // protegidos.
        ingresar(LoginRequest(datos.correo, datos.password))
            ?: throw ErrorRemoto("La cuenta fue creada. Inicia sesión para continuar.")
    }

    suspend fun ingresar(datos: LoginRequest): UsuarioDto? = ejecutar {
        val respuesta = api.iniciarSesion(datos)
        if (respuesta.code() == 401 || respuesta.code() == 403) return@ejecutar null
        if (!respuesta.isSuccessful) throw error(respuesta)
        val cuerpo = respuesta.body()
        val usuario = validar(cuerpo?.usuario, datos.correo)
        val token = cuerpo?.token
        if (token.isNullOrBlank())
            throw ErrorRemoto("El servidor no entregó un token de sesión válido.")
        guardarToken(token)
        usuario
    }

    private fun validar(usuario: UsuarioDto?, correo: String): UsuarioDto {
        if (usuario == null || usuario.idUsuario <= 0 || !usuario.correo.equals(correo, true))
            throw ErrorRemoto("El servidor entregó una respuesta de usuario incompleta.")
        return usuario
    }

    private fun error(respuesta: Response<*>): ErrorRemoto {
        val datos =
            runCatching {
                    Gson().fromJson(respuesta.errorBody()?.string(), ErrorServidor::class.java)
                }
                .getOrNull()
        val mensaje =
            when (respuesta.code()) {
                404 ->
                    "El servidor aún no tiene el módulo de usuarios. Revisa la configuración o usa el modo local."
                409 -> "Ese correo ya está registrado en el servidor"
                400 -> datos?.mensaje ?: "El servidor rechazó los datos. Revisa los campos."
                else ->
                    datos?.mensaje
                        ?: "No se pudo completar la operación en el servidor (${respuesta.code()})."
            }
        return ErrorRemoto(mensaje, datos?.errores.orEmpty())
    }

    private suspend fun <T> ejecutar(accion: suspend () -> T): T =
        try {
            accion()
        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            throw ErrorRemoto(
                "No se pudo conectar al servidor de usuarios. Revisa internet y la dirección configurada."
            )
        }
}
