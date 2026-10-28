package com.mastermartini.app.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mastermartini.app.data.local.dao.ContenidoDao
import com.mastermartini.app.data.local.dao.FavoritoDao
import com.mastermartini.app.data.local.dao.UsuarioDao
import com.mastermartini.app.data.local.entity.ContenidoEntity
import com.mastermartini.app.data.local.entity.FavoritoEntity
import com.mastermartini.app.data.local.entity.UsuarioEntity

// Base de datos local de Room para la aplicación.
@Database(
    entities = [UsuarioEntity::class, ContenidoEntity::class, FavoritoEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // Acceso a las operaciones DAO de usuarios.
    abstract fun usuarioDao(): UsuarioDao

    // Acceso a las operaciones DAO de contenidos.
    abstract fun contenidoDao(): ContenidoDao

    // Acceso a las operaciones DAO de favoritos.
    abstract fun favoritoDao(): FavoritoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Crea o devuelve la instancia única de la base de datos.
        fun obtener(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mastermartini_database"
                )
                    .fallbackToDestructiveMigration()
                    .build().also { INSTANCE = it }
            }
        }
    }
}