package com.mastermartini.app.data.remote

import retrofit2.http.*

data class RecetasExternasRespuesta(val meals: List<RecetaExterna>? = null)

data class RecetaExterna(
    val idMeal: String,
    val strMeal: String,
    val strCategory: String?,
    val strArea: String?,
    val strInstructions: String?,
    val strMealThumb: String?,
    val strSource: String?,
)

interface RecetaExternaApi {
    @GET("search.php") suspend fun buscar(@Query("s") nombre: String): RecetasExternasRespuesta

    @GET("lookup.php") suspend fun consultar(@Query("i") id: String): RecetasExternasRespuesta
}
