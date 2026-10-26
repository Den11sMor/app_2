package com.mastermartini.app.data.local.dao

import androidx.room.*
import com.mastermartini.app.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritoDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun agregar(favorito: FavoritoEntity)
    @Query("DELETE FROM favoritos WHERE usuarioId = :usuarioId AND contenidoId = :contenidoId")
    suspend fun quitar(usuarioId: Long, contenidoId: Int)
    @Query("SELECT contenidoId FROM favoritos WHERE usuarioId = :usuarioId")
    fun observarIds(usuarioId: Long): Flow<List<Int>>
}
