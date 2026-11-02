package com.mastermartini.app

import android.content.Context
import android.media.MediaMetadataRetriever
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mastermartini.app.data.local.database.AppDatabase
import com.mastermartini.app.data.local.entity.*
import com.mastermartini.app.data.repository.ContenidoRepository
import com.mastermartini.app.ui.components.ReproductorVideo
import com.mastermartini.app.ui.theme.MasterMartiniTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VideosTest {
    @get:Rule val compose = createComposeRule()

    @Test fun ambosVideosTienenImagenSonidoYDuracionReal() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        for ((recurso, duracionEsperada) in listOf(
            R.raw.tecnicas_chocolateria to 85_500L,
            R.raw.decoracion_tortas to 78_600L
        )) {
            val retriever = MediaMetadataRetriever()
            try {
                context.resources.openRawResourceFd(recurso).use {
                    retriever.setDataSource(it.fileDescriptor, it.startOffset, it.length)
                }
                val duracion = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)!!.toLong()
                assertTrue(kotlin.math.abs(duracion - duracionEsperada) < 1500)
                assertEquals("yes", retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_AUDIO))
                assertEquals("960", retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH))
                val frame = retriever.getFrameAtTime(1_000_000)
                assertNotNull(frame)
                frame?.recycle()
            } finally { retriever.release() }
        }
    }

    @Test fun actualizarCatalogoConservaFavoritosDeLaVersionAnterior() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        try {
            val id = db.usuarioDao().insertar(UsuarioEntity(nombre = "Luis Cid", correo = "luis@test.cl",
                telefono = "912345678", claveHash = "hash", sal = "sal"))
            db.contenidoDao().insertarTodos(listOf(ContenidoEntity(4, "Técnicas de chocolatería", "Video",
                "Chocolatería", "12 minutos", "Guía anterior", "Sin archivo de video")))
            db.favoritoDao().agregar(FavoritoEntity(id, 4))
            val video = ContenidoRepository(db.contenidoDao()).contenidos.first().first { it.id == 4 }
            assertEquals("1 min 25 s", video.duracion)
            assertTrue(video.descripcion.contains("narración") || video.descripcion.contains("voz"))
            assertEquals(listOf(4), db.favoritoDao().observarIds(id).first())
        } finally { db.close() }
    }

    @Test fun controlesPermitenReproducirPausarYAmpliar() {
        compose.setContent { MasterMartiniTheme { ReproductorVideo(R.raw.tecnicas_chocolateria) } }
        compose.waitUntil(20_000) {
            compose.onAllNodes(hasText("Reproducir") and isEnabled()).fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithText("Reproducir").performClick()
        compose.onNodeWithText("Pausar").assertExists().performClick()
        compose.onNodeWithText("Reproducir").assertExists()
        compose.onNodeWithContentDescription("Pantalla completa").performClick()
        compose.onNodeWithContentDescription("Salir de pantalla completa").assertExists().performClick()
        compose.onNodeWithContentDescription("Pantalla completa").assertExists()
    }
}
