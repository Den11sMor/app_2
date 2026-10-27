package com.mastermartini.app.data.local.dao

import androidx.room.*
import com.mastermartini.app.data.local.entity.UsuarioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {
    @Insert suspend fun insertar(usuario: UsuarioEntity): Long
    @Query("SELECT * FROM usuarios WHERE correo = :correo LIMIT 1")
    suspend fun buscarPorCorreo(correo: String): UsuarioEntity?
    @Query("SELECT * FROM usuarios WHERE id = :id")
    fun observar(id: Long): Flow<UsuarioEntity?>
    @Query("UPDATE usuarios SET nombre = :nombre, telefono = :telefono WHERE id = :id")
    suspend fun actualizarPerfil(id: Long, nombre: String, telefono: String)
    @Query("UPDATE usuarios SET intereses = :intereses WHERE id = :id")
    suspend fun actualizarIntereses(id: Long, intereses: String)
    @Query("UPDATE usuarios SET foto = :foto WHERE id = :id")
    suspend fun actualizarFoto(id: Long, foto: String)
}
