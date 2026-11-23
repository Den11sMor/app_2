package com.mastermartini.app.data.remote
// Contrato compartido. Cada interfaz concreta declara las rutas de su módulo.
interface ContenidoApi {
    suspend fun listar(): List<ContenidoRespuesta>

    suspend fun consultar(id: Long): ContenidoRespuesta

    suspend fun crear(datos: ContenidoSolicitud): ContenidoRespuesta

    suspend fun actualizar(id: Long, datos: ContenidoSolicitud): ContenidoRespuesta

    suspend fun eliminar(id: Long)
}
