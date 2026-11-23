package com.mastermartini.app.data.repository

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import com.mastermartini.app.data.local.dao.UsuarioDao
import com.mastermartini.app.data.local.entity.UsuarioEntity
import com.mastermartini.app.data.remote.RetrofitClient
import com.mastermartini.app.data.remote.dto.LoginRequest
import com.mastermartini.app.data.remote.dto.RegistroRequest
import com.mastermartini.app.domain.model.Usuario
import com.mastermartini.app.utils.ClaveSegura
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import java.util.Locale

// Repositorio para gestionar el registro, inicio de sesión y perfil conectando Retrofit y Room.
class UsuarioRepository(context: Context, private val dao: UsuarioDao) {
    private val preferencias = context.getSharedPreferences("sesion", Context.MODE_PRIVATE)
    private val sesion = MutableStateFlow(preferencias.getLong("usuario_id", 0))
    val idActual: Long get() = sesion.value

    // Flujo que observa los cambios del usuario actualmente logueado.
    @OptIn(ExperimentalCoroutinesApi::class)
    val usuario: Flow<Usuario?> = sesion.flatMapLatest { id ->
        if (id == 0L) flowOf(null) else dao.observar(id).map { entidad ->
            entidad?.let {
                Usuario(it.id, it.nombre, it.correo, it.telefono,
                    it.intereses.split("|").filter(String::isNotBlank), it.foto)
            }
        }
    }

    // Registra un nuevo usuario enviando la solicitud a Spring Boot (campo password) y guardando copia local en Room.
    suspend fun registrar(nombre: String, correo: String, telefono: String, clave: String): String? =
        withContext(Dispatchers.IO) {
            val email = correo.trim().lowercase(Locale.ROOT)

            // 1. Comprueba si el correo ya existe en la base de datos local Room
            val usuarioExistenteLocal = dao.buscarPorCorreo(email)
            if (usuarioExistenteLocal != null) {
                return@withContext "Ese correo ya está registrado"
            }

            // 2. Intenta registrar en el servidor Spring Boot mediante Retrofit
            var idServidor: Long = 0
            try {
                val respuestaApi = RetrofitClient.usuarioApi.registrarUsuario(
                    RegistroRequest(
                        nombre = nombre.trim(),
                        correo = email,
                        telefono = telefono.trim(),
                        password = clave
                    )
                )
                if (respuestaApi.isSuccessful && respuestaApi.body() != null) {
                    idServidor = respuestaApi.body()!!.idUsuario
                } else if (respuestaApi.code() == 400 || respuestaApi.code() == 409) {
                    return@withContext "Ese correo ya está registrado en el servidor"
                }
            } catch (_: Exception) {
                // Si el servidor HTTP está fuera de línea, se continúa registrando localmente en Room
            }

            // 3. Guarda el nuevo usuario en Room (con id = 0 para que Room auto-genere la clave primaria local)
            val sal = ClaveSegura.crearSal()
            try {
                val idInsertado = dao.insertar(
                    UsuarioEntity(
                        id = 0,
                        nombre = nombre.trim(),
                        correo = email,
                        telefono = telefono.trim(),
                        claveHash = ClaveSegura.hash(clave, sal),
                        sal = sal
                    )
                )
                guardarSesion(if (idInsertado > 0) idInsertado else if (idServidor > 0) idServidor else 1L)
                return@withContext null
            } catch (_: SQLiteConstraintException) {
                return@withContext "Ese correo ya está registrado"
            } catch (_: Exception) {
                return@withContext "No se pudo guardar el registro local"
            }
        }

    // Valida las credenciales de inicio de sesión conectando con Spring Boot (campo password) y fallback local en Room.
    suspend fun ingresar(correo: String, clave: String): Boolean = withContext(Dispatchers.IO) {
        val email = correo.trim().lowercase(Locale.ROOT)

        // 1. Intenta autenticar contra el backend Spring Boot mediante Retrofit
        try {
            val respuesta = RetrofitClient.usuarioApi.iniciarSesion(LoginRequest(email, password = clave))
            if (respuesta.isSuccessful && respuesta.body() != null) {
                val body = respuesta.body()!!
                val userDto = body.usuario
                val usuarioLocal = dao.buscarPorCorreo(email)
                if (usuarioLocal == null) {
                    val sal = ClaveSegura.crearSal()
                    val idInsertado = dao.insertar(
                        UsuarioEntity(
                            id = if ((userDto?.idUsuario ?: 0) > 0) userDto!!.idUsuario else 0,
                            nombre = userDto?.nombre?.ifBlank { "Usuario" } ?: "Usuario",
                            correo = email,
                            telefono = userDto?.telefono.orEmpty(),
                            claveHash = ClaveSegura.hash(clave, sal),
                            sal = sal,
                            foto = userDto?.foto.orEmpty()
                        )
                    )
                    guardarSesion(idInsertado)
                } else {
                    guardarSesion(usuarioLocal.id)
                }
                return@withContext true
            }
        } catch (_: Exception) {
            // Si el servidor HTTP está fuera de línea, procede con autenticación local
        }

        // 2. Autenticación local en Room si el servidor no estuvo disponible o respondió error
        val usuarioLocal = dao.buscarPorCorreo(email)
        if (usuarioLocal != null && ClaveSegura.verificar(clave, usuarioLocal.sal, usuarioLocal.claveHash)) {
            guardarSesion(usuarioLocal.id)
            true
        } else false
    }

    // Actualiza el nombre y teléfono del perfil en segundo plano.
    suspend fun guardarPerfil(nombre: String, telefono: String) = withContext(Dispatchers.IO) {
        check(idActual != 0L)
        dao.actualizarPerfil(idActual, nombre.trim(), telefono.trim())
    }

    // Actualiza los intereses seleccionados en segundo plano.
    suspend fun guardarIntereses(intereses: Set<String>) = withContext(Dispatchers.IO) {
        check(idActual != 0L)
        dao.actualizarIntereses(idActual, intereses.sorted().joinToString("|"))
    }

    // Guarda la ruta de la foto de perfil en segundo plano.
    suspend fun guardarFoto(ruta: String) = withContext(Dispatchers.IO) {
        check(idActual != 0L)
        dao.actualizarFoto(idActual, ruta)
    }

    // Cierra la sesión activa.
    fun cerrarSesion() = guardarSesion(0)

    // Guarda el ID del usuario en SharedPreferences.
    private fun guardarSesion(id: Long) {
        preferencias.edit().putLong("usuario_id", id).apply()
        sesion.value = id
    }
}
