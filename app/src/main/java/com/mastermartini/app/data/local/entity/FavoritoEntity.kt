package com.mastermartini.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "favoritos",
    primaryKeys = ["usuarioId", "contenidoId"],
    foreignKeys = [
        ForeignKey(entity = UsuarioEntity::class, parentColumns = ["id"], childColumns = ["usuarioId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = ContenidoEntity::class, parentColumns = ["id"], childColumns = ["contenidoId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("contenidoId")]
)
data class FavoritoEntity(val usuarioId: Long, val contenidoId: Int)
