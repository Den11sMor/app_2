package com.mastermartini.app.domain.repository


import com.mastermartini.app.domain.model.Usuario
import kotlinx.coroutines.flow.Flow

// Define las operaciones disponibles para gestionar usuarios
interface UsuarioRepository {

    // Guarda un usario y devuelve su identificador
    suspend fun insertar(usuario: Usuario): Long

    // Busca un usuario por el correo electronico
    suspend fun obtenerPorCorreo(correo: String): Usuario?

    //Observa todos los usuarios por su identificador
    fun obtenerTodos(): Flow<List<Usuario>>

    // Elimina un usuario en especifico por su identificador
    suspend fun eliminarPorId(id: Int)
}