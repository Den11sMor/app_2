package com.mastermartini.app

import com.mastermartini.app.data.remote.RecetaExternaApi
import com.mastermartini.app.data.repository.RecetaExternaRepository
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.junit.Assert.*
import org.junit.Assume.assumeTrue
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TheMealDbLiveTest {
    // Se activa explícitamente con -PliveApi=true; la suite cotidiana no depende de internet.
    @Test
    fun buscarYConsultarUnaRecetaReal() = runBlocking {
        assumeTrue("Prueba externa opcional", java.lang.Boolean.getBoolean("mastermartini.live"))
        val cliente = OkHttpClient.Builder().callTimeout(30, TimeUnit.SECONDS).build()
        val api =
            Retrofit.Builder()
                .baseUrl("https://www.themealdb.com/api/json/v1/1/")
                .client(cliente)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RecetaExternaApi::class.java)
        val repository = RecetaExternaRepository(api)
        val lista = repository.buscar("cake")
        assertTrue("TheMealDB debe entregar recetas", lista.isNotEmpty())
        val receta = repository.consultar(lista.first().idMeal)
        assertEquals(lista.first().idMeal, receta?.idMeal)
        assertFalse(receta?.strInstructions.isNullOrBlank())
        assertTrue(receta?.strMealThumb?.startsWith("https://") == true)
    }
}
