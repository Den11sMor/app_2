package com.mastermartini.app

import android.app.Application
import com.mastermartini.app.data.local.database.AppDatabase
import com.mastermartini.app.data.repository.*

class MasterMartiniApplication : Application() {
    private val database by lazy { AppDatabase.obtener(this) }
    val usuarios by lazy { UsuarioRepository(this, database.usuarioDao()) }
    val contenidos by lazy { ContenidoRepository(database.contenidoDao()) }
    val favoritos by lazy { FavoritoRepository(database.favoritoDao()) }
}
