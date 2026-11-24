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

    val usuariosRemotos: Boolean
        get() = preferencias.getBoolean("usuarios_remotos", true)

    fun guardarToken(token: String) {
        preferencias.edit().putString("token", token).apply()
    }

    fun cerrarSesion() {
        preferencias.edit().remove("token").apply()
    }

    fun guardar(url: String, usuariosRemotos: Boolean = this.usuariosRemotos) {
        val validada = ValidacionesContenido.servidor(url)
        if (validada != this.url || usuariosRemotos != this.usuariosRemotos) cerrarSesion()
        preferencias
            .edit()
            .putString("url", validada)
            .putBoolean("usuarios_remotos", usuariosRemotos)
            .apply()
    }

    private fun retrofit(base: String, autenticar: Boolean = false): Retrofit {
        val cliente =
            OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .followRedirects(false)
                .followSslRedirects(false)
                .addInterceptor { chain ->
                    val solicitud = chain.request().newBuilder()
                    // Solo el backend propio recibe el JWT. Registro y login deben funcionar sin
                    // token previo.
                    val ruta = chain.request().url.encodedPath
                    val publica =
                        ruta.endsWith("/api/usuarios/login") ||
                            ruta.endsWith("/api/usuarios/registro")
                    if (autenticar && !publica) {
                        val token = preferencias.getString("token", "").orEmpty()
                        if (token.isNotBlank()) solicitud.header("Authorization", "Bearer $token")
                    }
                    chain.proceed(solicitud.build())
                }
                .build()
        return Retrofit.Builder()
            .baseUrl(base)
            .client(cliente)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // La URL y la sesión JWT son las mismas para usuarios, cursos, recetas y videos. No hay otro
    // cliente
    // fijo para el emulador.
    fun repositorio(tipo: String): CatalogoRemoto {
        val servidor = retrofit(url, true)
        return when (tipo) {
            "Curso" -> CursoRepository(servidor.create(CursoApi::class.java))
            "Video" -> VideoRepository(servidor.create(VideoApi::class.java))
            else -> RecetaRepository(servidor.create(RecetaApi::class.java))
        }
    }

    fun usuarios() =
        UsuarioRemotoRepository(
            retrofit(url, true)
                .create(com.mastermartini.app.data.remote.api.UsuarioApi::class.java),
            ::guardarToken,
        )

    // TheMealDB usa otro host y nunca recibe el token de sesión del backend del equipo.
    fun recetasExternas() =
        RecetaExternaRepository(
            retrofit("https://www.themealdb.com/api/json/v1/1/")
                .create(RecetaExternaApi::class.java)
        )
}
