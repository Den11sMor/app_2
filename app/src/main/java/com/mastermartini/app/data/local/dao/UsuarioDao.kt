package com.mastermartini.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mastermartini.app.data.local.entity.UsuarioEntity
import kotlinx.coroutines.flow.Flow

// Defeine las operaciones disponibles para los usuarios
@Dao
interface UsuarioDao {

    // Guarda un usario y devuelve su identificador
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(usuario: UsuarioEntity): Long

    // Busca un usuario por el correo electronico
    @Query("SELECT * FROM usuarios WHERE correo = :correo LIMIT 1")
    suspend fun obtenerPorCorreo(correo: String): UsuarioEntity?

    //Observa todos los usuarios por su identificador
    @Query("SELECT * FROM usuarios ORDER BY nombre ASC")
    fun obtenerTodos(): Flow<List<UsuarioEntity>>

    // Elimina un usuario en especifico por su identificador
    @Query("DELETE FROM usuarios WHERE id = :id")
    suspend fun eliminarPorId(id: Int)
}