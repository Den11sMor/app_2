package com.mastermartini.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contenidos")
data class ContenidoEntity(
    @PrimaryKey val id: Int,
    val titulo: String,
    val tipo: String,
    val categoria: String,
    val duracion: String,
    val descripcion: String,
    val detalle: String
)
