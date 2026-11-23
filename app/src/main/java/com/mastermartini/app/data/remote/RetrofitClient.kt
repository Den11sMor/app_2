package com.mastermartini.app.data.remote

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// Configuración central de Retrofit para conectar Android con Spring Boot.
object RetrofitClient {

    // Dirección del backend Spring Boot.
    // 10.0.2.2 permite que el emulador Android acceda al localhost del computador.
    private const val BASE_URL = "http://10.0.2.2:8080/"

    // Configuración para convertir respuestas JSON en objetos Kotlin.
    private val gson: Gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        .create()

    // Permite visualizar las solicitudes y respuestas HTTP en Logcat.
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Configuración del cliente HTTP.
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    // Instancia principal de Retrofit.
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    // Instancia reutilizable de la API de usuarios de Retrofit.
    val usuarioApi: com.mastermartini.app.data.remote.api.UsuarioApi by lazy {
        retrofit.create(com.mastermartini.app.data.remote.api.UsuarioApi::class.java)
    }
}
