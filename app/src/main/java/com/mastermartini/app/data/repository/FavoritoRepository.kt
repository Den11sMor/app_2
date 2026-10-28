package com.mastermartini.app.data.repository

import com.mastermartini.app.data.local.dao.FavoritoDao
import com.mastermartini.app.data.local.entity.FavoritoEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Repositorio para consultar y alternar favoritos del usuario.
class FavoritoRepository(private val dao: FavoritoDao) {

    // Observa la lista de IDs de contenidos guardados como favoritos.
    fun observarIds(usuarioId: Long) = dao.observarIds(usuarioId)

    // Agrega o elimina un favorito ejecutando la operación en el hilo de E/S.
    suspend fun cambiar(usuarioId: Long, contenidoId: Int, guardado: Boolean) = withContext(Dispatchers.IO) {
        check(usuarioId != 0L)
        if (guardado) dao.quitar(usuarioId, contenidoId)
        else dao.agregar(FavoritoEntity(usuarioId, contenidoId))
    }
}