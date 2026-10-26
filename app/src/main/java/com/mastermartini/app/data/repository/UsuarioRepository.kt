package com.mastermartini.app.data.repository

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import com.mastermartini.app.data.local.dao.UsuarioDao
import com.mastermartini.app.data.local.entity.UsuarioEntity
import com.mastermartini.app.domain.model.Usuario
import com.mastermartini.app.utils.ClaveSegura
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import java.util.Locale

class UsuarioRepository(context: Context, private val dao: UsuarioDao) {
    private val preferencias = context.getSharedPreferences("sesion", Context.MODE_PRIVATE)
    private val sesion = MutableStateFlow(preferencias.getLong("usuario_id", 0))
    val idActual: Long get() = sesion.value

    @OptIn(ExperimentalCoroutinesApi::class)
    val usuario: Flow<Usuario?> = sesion.flatMapLatest { id ->
        if (id == 0L) flowOf(null) else dao.observar(id).map { entidad ->
            entidad?.let {
                Usuario(it.id, it.nombre, it.correo, it.telefono,
                    it.intereses.split("|").filter(String::isNotBlank), it.foto)
            }
        }
    }

    suspend fun registrar(nombre: String, correo: String, telefono: String, clave: String): String? =
        withContext(Dispatchers.IO) {
            val email = correo.trim().lowercase(Locale.ROOT)
            if (dao.buscarPorCorreo(email) != null) return@withContext "Ese correo ya está registrado"
            val sal = ClaveSegura.crearSal()
            try {
                val id = dao.insertar(UsuarioEntity(
                    nombre = nombre.trim(), correo = email, telefono = telefono.trim(),
                    claveHash = ClaveSegura.hash(clave, sal), sal = sal
                ))
                guardarSesion(id)
                null
            } catch (_: SQLiteConstraintException) {
                "Ese correo ya está registrado"
            }
        }

    suspend fun ingresar(correo: String, clave: String): Boolean = withContext(Dispatchers.IO) {
        val usuario = dao.buscarPorCorreo(correo.trim().lowercase(Locale.ROOT))
        if (usuario != null && ClaveSegura.verificar(clave, usuario.sal, usuario.claveHash)) {
            guardarSesion(usuario.id)
            true
        } else false
    }

    suspend fun guardarPerfil(nombre: String, telefono: String) {
        check(idActual != 0L)
        dao.actualizarPerfil(idActual, nombre.trim(), telefono.trim())
    }
    suspend fun guardarIntereses(intereses: Set<String>) {
        check(idActual != 0L)
        dao.actualizarIntereses(idActual, intereses.sorted().joinToString("|"))
    }
    suspend fun guardarFoto(ruta: String) {
        check(idActual != 0L)
        dao.actualizarFoto(idActual, ruta)
    }
    fun cerrarSesion() = guardarSesion(0)

    private fun guardarSesion(id: Long) {
        preferencias.edit().putLong("usuario_id", id).apply()
        sesion.value = id
    }
}
