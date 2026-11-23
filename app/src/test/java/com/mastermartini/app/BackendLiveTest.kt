package com.mastermartini.app

import com.mastermartini.app.data.remote.*
import com.mastermartini.app.data.remote.api.UsuarioApi
import com.mastermartini.app.data.remote.dto.*
import com.mastermartini.app.data.repository.*
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.junit.Assert.*
import org.junit.Assume.assumeTrue
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BackendLiveTest {
    // Se activa con -PliveBackend=true y requiere Spring Boot + MySQL local encendidos.
    @Test
    fun retrofitAutenticaYRealizaCrudEnLosTresModulos() = runBlocking {
        assumeTrue(
            "Prueba con backend real opcional",
            java.lang.Boolean.getBoolean("mastermartini.backendLive"),
        )
        val base = "http://127.0.0.1:8080/"
        val publico =
            Retrofit.Builder()
                .baseUrl(base)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        val api = publico.create(UsuarioApi::class.java)
        val correo = "prueba.${System.currentTimeMillis()}@mastermartini.test"
        var token = ""
        val usuarios = UsuarioRemotoRepository(api) { token = it }
        val usuario =
            usuarios.registrar(
                RegistroRequest("Usuario Prueba", correo, "912345678", "Martini2026")
            )
        assertTrue(usuario.idUsuario > 0)
        assertTrue(token.isNotBlank())
        val cliente =
            OkHttpClient.Builder()
                .callTimeout(30, TimeUnit.SECONDS)
                .addInterceptor { chain ->
                    chain.proceed(
                        chain
                            .request()
                            .newBuilder()
                            .header("Authorization", "Bearer $token")
                            .build()
                    )
                }
                .build()
        val retrofit =
            Retrofit.Builder()
                .baseUrl(base)
                .client(cliente)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        val repositorios =
            listOf<CatalogoRemoto>(
                CursoRepository(retrofit.create(CursoApi::class.java)),
                RecetaRepository(retrofit.create(RecetaApi::class.java)),
                VideoRepository(retrofit.create(VideoApi::class.java)),
            )
        val datos =
            ContenidoSolicitud(
                "Contenido desde Android",
                "Pastelería",
                "20 minutos",
                "Prueba Retrofit real",
                "Preparación o programa",
                "https://example.com/prueba.mp4",
            )
        for (repo in repositorios) {
            var id: Long? = null
            try {
                val creado = repo.crear(datos)
                id = creado.id
                assertEquals(datos.titulo, repo.consultar(creado.id).titulo)
                assertTrue(repo.listar().any { it.id == creado.id })
                assertEquals(
                    "Actualizado",
                    repo.actualizar(creado.id, datos.copy(titulo = "Actualizado")).titulo,
                )
                repo.eliminar(creado.id)
                id = null
                assertFalse(repo.listar().any { it.id == creado.id })
            } finally {
                id?.let { repo.eliminar(it) }
            }
        }
    }
}
