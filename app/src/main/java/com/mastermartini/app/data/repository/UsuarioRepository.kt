package com.mastermartini.app.data.repository

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import com.mastermartini.app.data.local.dao.UsuarioDao
import com.mastermartini.app.data.local.entity.UsuarioEntity
import com.mastermartini.app.data.remote.dto.LoginRequest
import com.mastermartini.app.data.remote.dto.RegistroRequest
import com.mastermartini.app.domain.model.Usuario
import com.mastermartini.app.utils.ClaveSegura
import java.util.Locale
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

// Room conserva la sesión del teléfono. El modo remoto se activa explícitamente en Configuración.
class UsuarioRepository(
    context: Context,
    private val dao: UsuarioDao,
    private val remoto: (() -> UsuarioRemotoRepository)? = null,
    private val usarRemoto: () -> Boolean = { false },
    private val cerrarRemota: () -> Unit = {},
) {
    private val preferencias = context.getSharedPreferences("sesion", Context.MODE_PRIVATE)
    private val sesion = MutableStateFlow(preferencias.getLong("usuario_id", 0))
    val idActual: Long
        get() = sesion.value

    @OptIn(ExperimentalCoroutinesApi::class)
    val usuario: Flow<Usuario?> =
        sesion.flatMapLatest { id ->
            if (id == 0L) flowOf(null)
            else
                dao.observar(id).map { entidad ->
                    entidad?.let {
                        Usuario(
                            it.id,
                            it.nombre,
                            it.correo,
                            it.telefono,
                            it.intereses.split("|").filter(String::isNotBlank),
                            it.foto,
                        )
                    }
                }
        }

    suspend fun registrar(
        nombre: String,
        correo: String,
        telefono: String,
        clave: String,
    ): String? =
        withContext(Dispatchers.IO) {
            val email = correo.trim().lowercase(Locale.ROOT)
            if (dao.buscarPorCorreo(email) != null)
                return@withContext "Ese correo ya está registrado"

            // En modo remoto primero se confirma la creación en Spring Boot; no se oculta un error
            // como éxito local.
            if (usarRemoto()) {
                val servicio =
                    remoto?.invoke()
                        ?: throw ErrorRemoto("Falta configurar el servidor de usuarios")
                servicio.registrar(RegistroRequest(nombre.trim(), email, telefono.trim(), clave))
            }
            val sal = ClaveSegura.crearSal()
            try {
                val idLocal =
                    dao.insertar(
                        UsuarioEntity(
                            nombre = nombre.trim(),
                            correo = email,
                            telefono = telefono.trim(),
                            claveHash = ClaveSegura.hash(clave, sal),
                            sal = sal,
                        )
                    )
                guardarSesion(idLocal)
                null
            } catch (e: CancellationException) {
                throw e
            } catch (_: SQLiteConstraintException) {
                "Ese correo ya está registrado"
            }
        }

    suspend fun ingresar(correo: String, clave: String): Boolean =
        withContext(Dispatchers.IO) {
            val email = correo.trim().lowercase(Locale.ROOT)
            val local = dao.buscarPorCorreo(email)
            if (usarRemoto()) {
                val servicio =
                    remoto?.invoke()
                        ?: throw ErrorRemoto("Falta configurar el servidor de usuarios")
                val usuarioServidor =
                    servicio.ingresar(LoginRequest(email, clave)) ?: return@withContext false
                val sal = ClaveSegura.crearSal()
                val hash = ClaveSegura.hash(clave, sal)
                // El ID de MySQL no se usa como clave de Room: evita colisiones con cuentas ya
                // guardadas.
                val idLocal =
                    if (local == null) {
                        dao.insertar(
                            UsuarioEntity(
                                nombre = usuarioServidor.nombre.ifBlank { "Usuario" },
                                correo = email,
                                telefono = usuarioServidor.telefono.orEmpty(),
                                claveHash = hash,
                                sal = sal,
                            )
                        )
                    } else {
                        dao.actualizarCredenciales(local.id, hash, sal)
                        local.id
                    }
                guardarSesion(idLocal)
                return@withContext true
            }
            // El acceso local es un modo de práctica elegido por el usuario, no un reemplazo de un
            // HTTP 401.
            if (local != null && ClaveSegura.verificar(clave, local.sal, local.claveHash)) {
                guardarSesion(local.id)
                true
            } else false
        }

    // Actualiza el nombre y teléfono del perfil en segundo plano.
    suspend fun guardarPerfil(nombre: String, telefono: String) =
        withContext(Dispatchers.IO) {
            check(idActual != 0L)
            dao.actualizarPerfil(idActual, nombre.trim(), telefono.trim())
        }

    // Actualiza los intereses seleccionados en segundo plano.
    suspend fun guardarIntereses(intereses: Set<String>) =
        withContext(Dispatchers.IO) {
            check(idActual != 0L)
            dao.actualizarIntereses(idActual, intereses.sorted().joinToString("|"))
        }

    // Guarda la ruta de la foto de perfil en segundo plano.
    suspend fun guardarFoto(ruta: String) =
        withContext(Dispatchers.IO) {
            check(idActual != 0L)
            dao.actualizarFoto(idActual, ruta)
        }

    // Cierra la sesión activa.
    fun cerrarSesion() {
        cerrarRemota()
        guardarSesion(0)
    }

    // Guarda el ID del usuario en SharedPreferences.
    private fun guardarSesion(id: Long) {
        preferencias.edit().putLong("usuario_id", id).apply()
        sesion.value = id
    }
}
