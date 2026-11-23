package com.mastermartini.app

import android.content.Context
import android.content.SharedPreferences
import com.mastermartini.app.data.local.dao.UsuarioDao
import com.mastermartini.app.data.local.entity.UsuarioEntity
import com.mastermartini.app.data.remote.api.UsuarioApi
import com.mastermartini.app.data.remote.dto.*
import com.mastermartini.app.data.repository.*
import com.mastermartini.app.utils.ClaveSegura
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mockito.*
import retrofit2.Response

// Revisa los problemas detectados al unir el login del compañero con los módulos de contenidos.
class UsuarioIntegracionTest {
    private fun contexto(): Context {
        val contexto = mock(Context::class.java)
        val preferencias = mock(SharedPreferences::class.java)
        val editor = mock(SharedPreferences.Editor::class.java)
        `when`(contexto.getSharedPreferences("sesion", Context.MODE_PRIVATE))
            .thenReturn(preferencias)
        `when`(preferencias.edit()).thenReturn(editor)
        `when`(editor.putLong(anyString(), anyLong())).thenReturn(editor)
        return contexto
    }

    @Test
    fun modoLocalConservaRegistroYLoginSinLlamarAlServidor() = runTest {
        val dao = UsuariosFalsos()
        val api = ApiUsuariosFalsa()
        val repo = UsuarioRepository(contexto(), dao, { UsuarioRemotoRepository(api) }, { false })
        assertNull(repo.registrar("Luis Prueba", " luis@test.cl ", "912345678", "Martini2026"))
        assertEquals(0, api.llamadas)
        assertEquals(
            "Ese correo ya está registrado",
            repo.registrar("Luis Prueba", "luis@test.cl", "912345678", "Martini2026"),
        )
        repo.cerrarSesion()
        assertFalse(repo.ingresar("luis@test.cl", "incorrecta"))
        assertTrue(repo.ingresar("luis@test.cl", "Martini2026"))
        assertTrue(repo.idActual > 0)
        assertTrue(dao.filas.values.none { it.claveHash == "Martini2026" })
    }

    @Test
    fun rechazoRemotoNoUsaCredencialLocalAunqueCoincida() = runTest {
        val dao = UsuariosFalsos()
        val api = ApiUsuariosFalsa()
        val sal = ClaveSegura.crearSal()
        dao.insertar(
            UsuarioEntity(
                nombre = "Luis",
                correo = "luis@test.cl",
                telefono = "",
                claveHash = ClaveSegura.hash("Martini2026", sal),
                sal = sal,
            )
        )
        api.codigo = 401
        val repo = UsuarioRepository(contexto(), dao, { UsuarioRemotoRepository(api) }, { true })
        assertFalse(repo.ingresar("luis@test.cl", "Martini2026"))
        assertEquals(0L, repo.idActual)
    }

    @Test
    fun idRemotoNoPisaOtraCuentaLocalYActualizaCacheDeLaClave() = runTest {
        val dao = UsuariosFalsos()
        val api = ApiUsuariosFalsa()
        dao.insertar(
            UsuarioEntity(
                nombre = "Otra persona",
                correo = "otra@test.cl",
                telefono = "",
                claveHash = "hash",
                sal = "sal",
            )
        )
        val repo = UsuarioRepository(contexto(), dao, { UsuarioRemotoRepository(api) }, { true })
        assertTrue(repo.ingresar("luis@test.cl", "Martini2026"))
        assertEquals(2L, repo.idActual)
        assertEquals("otra@test.cl", dao.filas[1]!!.correo)
        assertTrue(repo.ingresar("luis@test.cl", "NuevaClave2026"))
        val cache = dao.buscarPorCorreo("luis@test.cl")!!
        assertTrue(ClaveSegura.verificar("NuevaClave2026", cache.sal, cache.claveHash))
        assertFalse(ClaveSegura.verificar("Martini2026", cache.sal, cache.claveHash))
    }

    @Test
    fun registroFallidoEnServidorNoCreaCuentaLocal() = runTest {
        val dao = UsuariosFalsos()
        val api = ApiUsuariosFalsa()
        api.codigo = 409
        val repo = UsuarioRepository(contexto(), dao, { UsuarioRemotoRepository(api) }, { true })
        try {
            repo.registrar("Luis Prueba", "luis@test.cl", "912345678", "Martini2026")
            fail("Debía rechazar el registro")
        } catch (e: ErrorRemoto) {
            assertTrue(e.message!!.contains("registrado"))
        }
        assertTrue(dao.filas.isEmpty())
        assertEquals(0L, repo.idActual)
    }

    @Test
    fun registroRemotoConfirmadoGuardaUnaCopiaLocal() = runTest {
        val dao = UsuariosFalsos()
        val api = ApiUsuariosFalsa()
        val repo = UsuarioRepository(contexto(), dao, { UsuarioRemotoRepository(api) }, { true })
        assertNull(repo.registrar("Luis Prueba", "luis@test.cl", "912345678", "Martini2026"))
        assertEquals(2, api.llamadas)
        assertNotNull(dao.buscarPorCorreo("luis@test.cl"))
    }

    @Test
    fun backendSinUsuariosYRespuestaIncompletaSeMuestranComoError() = runTest {
        val api = ApiUsuariosFalsa()
        val repo = UsuarioRemotoRepository(api)
        api.codigo = 404
        try {
            repo.ingresar(LoginRequest("luis@test.cl", "Clave2026"))
            fail("Debía fallar")
        } catch (e: ErrorRemoto) {
            assertTrue(e.message!!.contains("módulo de usuarios"))
        }
        api.codigo = 200
        api.usuario = api.usuario.copy(correo = "otro@test.cl")
        try {
            repo.ingresar(LoginRequest("luis@test.cl", "Clave2026"))
            fail("Debía rechazar el usuario incorrecto")
        } catch (e: ErrorRemoto) {
            assertTrue(e.message!!.contains("incompleta"))
        }
    }
}

private class ApiUsuariosFalsa : UsuarioApi {
    var codigo = 200
    var llamadas = 0
    var usuario = UsuarioDto(1, "Luis Prueba", "luis@test.cl", "912345678")

    private fun <T> respuesta(body: T): Response<T> {
        llamadas++
        return if (codigo in 200..299) Response.success(body)
        else Response.error(codigo, "{}".toResponseBody("application/json".toMediaType()))
    }

    override suspend fun iniciarSesion(request: LoginRequest) =
        respuesta(LoginResponse("token-prueba", "Bearer", usuario))

    override suspend fun registrarUsuario(request: RegistroRequest) = respuesta(usuario)

    override suspend fun listarUsuarios() = respuesta(listOf(usuario))
}

private class UsuariosFalsos : UsuarioDao {
    val filas = linkedMapOf<Long, UsuarioEntity>()
    private var siguiente = 1L

    override suspend fun insertar(usuario: UsuarioEntity): Long {
        val id = if (usuario.id == 0L) siguiente++ else usuario.id
        check(id !in filas)
        filas[id] = usuario.copy(id = id)
        return id
    }

    override suspend fun buscarPorCorreo(correo: String) =
        filas.values.firstOrNull { it.correo == correo }

    override fun observar(id: Long) = flowOf(filas[id])

    override suspend fun actualizarCredenciales(id: Long, hash: String, sal: String) {
        filas[id]?.let { filas[id] = it.copy(claveHash = hash, sal = sal) }
    }

    override suspend fun actualizarPerfil(id: Long, nombre: String, telefono: String) {
        filas[id]?.let { filas[id] = it.copy(nombre = nombre, telefono = telefono) }
    }

    override suspend fun actualizarIntereses(id: Long, intereses: String) {
        filas[id]?.let { filas[id] = it.copy(intereses = intereses) }
    }

    override suspend fun actualizarFoto(id: Long, foto: String) {
        filas[id]?.let { filas[id] = it.copy(foto = foto) }
    }
}
