package com.mastermartini.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

//Entidad Room para almacenar usuarios localmente
@Entity(tableName = "usuarios")
data class UsuarioEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre : String,
    val correo: String,
    val telefono: String,
    val tipoNegocio: String,
    val tamanoNegocio: String
)