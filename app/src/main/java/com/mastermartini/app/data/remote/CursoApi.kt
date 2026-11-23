package com.mastermartini.app.data.remote

import retrofit2.http.*

interface CursoApi : ContenidoApi {
    @GET("api/cursos") override suspend fun listar(): List<ContenidoRespuesta>

    @GET("api/cursos/{id}") override suspend fun consultar(@Path("id") id: Long): ContenidoRespuesta

    @POST("api/cursos")
    override suspend fun crear(@Body datos: ContenidoSolicitud): ContenidoRespuesta

    @PUT("api/cursos/{id}")
    override suspend fun actualizar(
        @Path("id") id: Long,
        @Body datos: ContenidoSolicitud,
    ): ContenidoRespuesta

    @DELETE("api/cursos/{id}") override suspend fun eliminar(@Path("id") id: Long)
}
