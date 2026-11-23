package com.mastermartini.app.data.repository

import com.google.gson.Gson
import com.mastermartini.app.data.remote.*
import java.io.IOException
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException

interface CatalogoRemoto {
    suspend fun listar(): List<ContenidoRespuesta>

    suspend fun consultar(id: Long): ContenidoRespuesta

    suspend fun crear(datos: ContenidoSolicitud): ContenidoRespuesta

    suspend fun actualizar(id: Long, datos: ContenidoSolicitud): ContenidoRespuesta

    suspend fun eliminar(id: Long)
}

class ErrorRemoto(mensaje: String, val campos: Map<String, String> = emptyMap()) :
    Exception(mensaje)

open class ContenidoRemotoRepository(private val api: ContenidoApi) : CatalogoRemoto {
    override suspend fun listar() = ejecutar { api.listar() }

    override suspend fun consultar(id: Long) = ejecutar { api.consultar(id) }

    override suspend fun crear(datos: ContenidoSolicitud) = ejecutar { api.crear(datos) }

    override suspend fun actualizar(id: Long, datos: ContenidoSolicitud) = ejecutar {
        api.actualizar(id, datos)
    }

    override suspend fun eliminar(id: Long) = ejecutar { api.eliminar(id) }

    private suspend fun <T> ejecutar(accion: suspend () -> T): T =
        try {
            accion()
        } catch (e: CancellationException) {
            throw e
        } catch (e: HttpException) {
            val error =
                runCatching {
                        Gson()
                            .fromJson(
                                e.response()?.errorBody()?.string(),
                                ErrorServidor::class.java,
                            )
                    }
                    .getOrNull()
            throw ErrorRemoto(
                error?.mensaje
                    ?: when (e.code()) {
                        401 -> "Revisa la clave de API en la configuración."
                        404 -> "El contenido ya no existe. Actualiza la lista."
                        else -> "El servidor respondió con error ${e.code()}."
                    },
                error?.errores.orEmpty(),
            )
        } catch (e: IOException) {
            throw ErrorRemoto(
                "No se pudo conectar. Revisa internet, la dirección del servidor y que el backend esté encendido."
            )
        }
}
