package com.mastermartini.app.data.remote

import retrofit2.http.*

interface RecetaApi : ContenidoApi {
    @GET("api/recetas") override suspend fun listar(): List<ContenidoRespuesta>

    @GET("api/recetas/{id}")
    override suspend fun consultar(@Path("id") id: Long): ContenidoRespuesta

    @POST("api/recetas")
    override suspend fun crear(@Body datos: ContenidoSolicitud): ContenidoRespuesta

    @PUT("api/recetas/{id}")
    override suspend fun actualizar(
        @Path("id") id: Long,
        @Body datos: ContenidoSolicitud,
    ): ContenidoRespuesta

    @DELETE("api/recetas/{id}") override suspend fun eliminar(@Path("id") id: Long)
}
