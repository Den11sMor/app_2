package com.mastermartini.app

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mastermartini.app.data.local.database.AppDatabase
import com.mastermartini.app.data.local.entity.*
import com.mastermartini.app.data.repository.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PersistenciaTest {
    private lateinit var db: AppDatabase

    @Before fun preparar() {
        val contexto = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(contexto, AppDatabase::class.java).build()
    }

    @After fun cerrar() { db.close() }

    @Test fun favoritosSonUnicosYSeSeparanPorUsuario() = runBlocking {
        val usuario1 = db.usuarioDao().insertar(UsuarioEntity(nombre = "Luis", correo = "luis@test.cl",
            telefono = "912345678", claveHash = "hash", sal = "sal"))
        val usuario2 = db.usuarioDao().insertar(UsuarioEntity(nombre = "Denis", correo = "denis@test.cl",
            telefono = "912345678", claveHash = "hash", sal = "sal"))
        val contenido = ContenidoRepository(db.contenidoDao()).contenidos.first().first()
        val repo = FavoritoRepository(db.favoritoDao())
        repo.cambiar(usuario1, contenido.id, false)
        repo.cambiar(usuario1, contenido.id, false)
        assertEquals(listOf(contenido.id), repo.observarIds(usuario1).first())
        assertTrue(repo.observarIds(usuario2).first().isEmpty())
        repo.cambiar(usuario1, contenido.id, true)
        assertTrue(repo.observarIds(usuario1).first().isEmpty())
    }

    @Test fun perfilEInteresesSeActualizanSinCambiarLaCuenta() = runBlocking {
        val id = db.usuarioDao().insertar(UsuarioEntity(nombre = "Luis", correo = "luis@test.cl",
            telefono = "912345678", claveHash = "hash", sal = "sal"))
        db.usuarioDao().actualizarPerfil(id, "Luis Cid", "+56912345678")
        db.usuarioDao().actualizarIntereses(id, "Recetas|Cocina")
        val usuario = db.usuarioDao().observar(id).first()!!
        assertEquals("Luis Cid", usuario.nombre)
        assertEquals("Recetas|Cocina", usuario.intereses)
        assertEquals("luis@test.cl", usuario.correo)
        assertEquals("hash", usuario.claveHash)
    }

    @Test fun archivoDeBaseDeDatosConservaDatosAlReabrirse() = runBlocking {
        val contexto = ApplicationProvider.getApplicationContext<Context>()
        val nombre = "prueba_persistencia_${System.nanoTime()}.db"
        var archivo = Room.databaseBuilder(contexto, AppDatabase::class.java, nombre).build()
        try {
            val id = archivo.usuarioDao().insertar(UsuarioEntity(nombre = "Prueba", correo = "prueba@test.cl",
                telefono = "912345678", claveHash = "hash", sal = "sal"))
            archivo.close()
            archivo = Room.databaseBuilder(contexto, AppDatabase::class.java, nombre).build()
            assertEquals("Prueba", archivo.usuarioDao().observar(id).first()?.nombre)
        } finally {
            archivo.close()
            contexto.deleteDatabase(nombre)
        }
    }
}
