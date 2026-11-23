package com.mastermartini.app.data.remote

import android.content.Context
import com.mastermartini.app.data.repository.*
import com.mastermartini.app.utils.ValidacionesContenido
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Una configuración de red compartida, separada de Room y de las pantallas.
class NetworkModule(context: Context) {
    private val preferencias = context.getSharedPreferences("servidor", Context.MODE_PRIVATE)
    val url: String
        get() = preferencias.getString("url", "http://127.0.0.1:8080/")!!

    val clave: String
        get() = preferencias.getString("clave", "")!!

    fun guardar(url: String, clave: String) {
        val validada = ValidacionesContenido.servidor(url)
        require(clave.all { it.code in 32..126 }) {
            "La clave debe contener solo caracteres ASCII imprimibles"
        }
        preferencias.edit().putString("url", validada).putString("clave", clave.trim()).apply()
    }

    private fun retrofit(base: String, apiKey: String = ""): Retrofit {
        val cliente =
            OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .followRedirects(false)
                .followSslRedirects(false)
                .addInterceptor { chain ->
                    val solicitud = chain.request().newBuilder()
                    if (apiKey.isNotBlank()) solicitud.header("X-API-Key", apiKey)
                    chain.proceed(solicitud.build())
                }
                .build()
        return Retrofit.Builder()
            .baseUrl(base)
            .client(cliente)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun repositorio(tipo: String): CatalogoRemoto {
        val servidor = retrofit(url, clave)
        return when (tipo) {
            "Curso" -> CursoRepository(servidor.create(CursoApi::class.java))
            "Video" -> VideoRepository(servidor.create(VideoApi::class.java))
            else -> RecetaRepository(servidor.create(RecetaApi::class.java))
        }
    }

    fun recetasExternas() =
        RecetaExternaRepository(
            retrofit("https://www.themealdb.com/api/json/v1/1/")
                .create(RecetaExternaApi::class.java)
        )
}
