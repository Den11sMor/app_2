package com.mastermartini.app.data.repository

import com.mastermartini.app.data.local.dao.FavoritoDao
import com.mastermartini.app.data.local.entity.FavoritoEntity

class FavoritoRepository(private val dao: FavoritoDao) {
    fun observarIds(usuarioId: Long) = dao.observarIds(usuarioId)

    suspend fun cambiar(usuarioId: Long, contenidoId: Int, guardado: Boolean) {
        check(usuarioId != 0L)
        if (guardado) dao.quitar(usuarioId, contenidoId)
        else dao.agregar(FavoritoEntity(usuarioId, contenidoId))
    }
}
