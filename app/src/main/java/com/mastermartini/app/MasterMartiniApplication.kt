package com.mastermartini.app

import android.app.Application
import com.mastermartini.app.data.local.database.AppDatabase
import com.mastermartini.app.data.repository.ContenidoRepository
import com.mastermartini.app.data.repository.FavoritoRepository
import com.mastermartini.app.data.repository.UsuarioRepository

// Clase Application principal para inicializar la base de datos y repositorios globales.
class MasterMartiniApplication : Application() {

    // Instancia única y perezosa de la base de datos Room.
    private val database by lazy { AppDatabase.obtener(this) }

    // Repositorio global de usuarios.
    val usuarios by lazy { UsuarioRepository(this, database.usuarioDao()) }

    // Repositorio global de contenidos (recetas, videos, cursos).
    val contenidos by lazy { ContenidoRepository(database.contenidoDao()) }

    // Repositorio global de contenidos favoritos.
    val favoritos by lazy { FavoritoRepository(database.favoritoDao()) }
}