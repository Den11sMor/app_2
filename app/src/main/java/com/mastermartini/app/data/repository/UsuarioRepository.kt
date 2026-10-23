package com.mastermartini.app.data.repository

import com.mastermartini.app.data.local.dao.UsuarioDao
import com.mastermartini.app.data.local.entity.UsuarioEntity
import com.mastermartini.app.domain.model.Usuario
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Define y ejecuta las operaciones para gestionar usuarios.
class UsuarioRepository(
    private val usuarioDao: UsuarioDao
) {

    // Guarda un usuario y devuelve su identificador.
    suspend fun insertar(usuario: Usuario): Long {
        return usuarioDao.insertar(usuario.toEntity())
    }

    // Busca un usuario por su correo electrónico.
    suspend fun obtenerPorCorreo(correo: String): Usuario? {
        return usuarioDao.obtenerPorCorreo(correo)?.toDomain()
    }

    // Obtiene todos los usuarios almacenados.
    fun obtenerTodos(): Flow<List<Usuario>> {
        return usuarioDao.obtenerTodos().map { usuarios ->
            usuarios.map { it.toDomain() }
        }
    }

    // Elimina un usuario por su identificador.
    suspend fun eliminarPorId(id: Int) {
        usuarioDao.eliminarPorId(id)
    }
}

// Convierte el modelo de dominio en una entidad Room.
private fun Usuario.toEntity(): UsuarioEntity {
    return UsuarioEntity(
        id = id,
        nombre = nombre,
        correo = correo,
        telefono = telefono,
        tipoNegocio = tipoNegocio,
        tamanoNegocio = tamanoNegocio
    )
}

// Convierte una entidad Room en el modelo de dominio.
private fun UsuarioEntity.toDomain(): Usuario {
    return Usuario(
        id = id,
        nombre = nombre,
        correo = correo,
        telefono = telefono,
        tipoNegocio = tipoNegocio,
        tamanoNegocio = tamanoNegocio
    )
}