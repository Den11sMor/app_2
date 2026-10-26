package com.mastermartini.app.data.local.dao

import androidx.room.*
import com.mastermartini.app.data.local.entity.ContenidoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ContenidoDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarTodos(contenidos: List<ContenidoEntity>)
    @Query("SELECT * FROM contenidos ORDER BY id")
    fun observarTodos(): Flow<List<ContenidoEntity>>
}
