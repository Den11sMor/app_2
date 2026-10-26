package com.mastermartini.app.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mastermartini.app.data.local.dao.*
import com.mastermartini.app.data.local.entity.*

@Database(entities = [UsuarioEntity::class, ContenidoEntity::class, FavoritoEntity::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun contenidoDao(): ContenidoDao
    abstract fun favoritoDao(): FavoritoDao

    companion object {
        @Volatile private var instancia: AppDatabase? = null

        fun obtener(context: Context): AppDatabase = instancia ?: synchronized(this) {
            instancia ?: Room.databaseBuilder(
                context.applicationContext, AppDatabase::class.java, "master_martini.db"
            ).build().also { instancia = it }
        }
    }
}
