package com.mastermartini.app.data.remote

import retrofit2.http.*

// Retrofit transforma estas funciones suspendidas en solicitudes HTTP al módulo de videos.
// @Path coloca el ID en la ruta; @Body convierte los datos del formulario a JSON.
interface VideoApi : ContenidoApi {
    @GET("api/videos") override suspend fun listar(): List<ContenidoRespuesta>

    @GET("api/videos/{id}") override suspend fun consultar(@Path("id") id: Long): ContenidoRespuesta

    @POST("api/videos")
    override suspend fun crear(@Body datos: ContenidoSolicitud): ContenidoRespuesta

    @PUT("api/videos/{id}")
    override suspend fun actualizar(
        @Path("id") id: Long,
        @Body datos: ContenidoSolicitud,
    ): ContenidoRespuesta

    @DELETE("api/videos/{id}") override suspend fun eliminar(@Path("id") id: Long)
}
