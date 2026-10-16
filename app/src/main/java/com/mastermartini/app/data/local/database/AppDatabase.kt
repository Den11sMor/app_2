package com.mastermartini.app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mastermartini.app.data.local.entity.UsuarioEntity

// Define la base de datos local de la aplicación.
@Database(
    entities = [
        UsuarioEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // Proporciona acceso a las operaciones de usuarios.
    abstract fun usuarioDao(): com.mastermartini.app.data.local.dao.UsuarioDao
}