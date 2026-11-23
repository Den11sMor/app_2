package com.mastermartini.app.data.remote

import retrofit2.http.*

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
