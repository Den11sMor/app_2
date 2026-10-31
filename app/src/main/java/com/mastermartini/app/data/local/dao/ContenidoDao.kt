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

    // Actualiza los videos de cuentas ya existentes sin reemplazar filas ni borrar favoritos.
    @Query("""
        UPDATE contenidos SET duracion = :duracion, descripcion = :descripcion, detalle = :detalle
        WHERE id = :id AND tipo = 'Video'
        AND (duracion != :duracion OR descripcion != :descripcion OR detalle != :detalle)
    """)
    suspend fun actualizarVideo(id: Int, duracion: String, descripcion: String, detalle: String)
}
