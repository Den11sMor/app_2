package com.mastermartini.app

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.content.ContentValues
import android.os.Build
import android.provider.MediaStore
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.lifecycle.ViewModelStore
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.mastermartini.app.ui.viewmodels.PerfilViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

// Pruebas optativas: requieren Spring Boot/MySQL en 8080 y adb reverse tcp:8080 tcp:8080.
// Cada prueba crea su propia cuenta y solo elimina los contenidos que ella misma creó.
@RunWith(AndroidJUnit4::class)
class FlujosRemotosTest {
    @get:Rule val compose = createEmptyComposeRule()
    private val app get() = ApplicationProvider.getApplicationContext<MasterMartiniApplication>()
    private val video = "https://raw.githubusercontent.com/Den11sMor/app_2/cb965d2dd2f9ee28b9f4b7cc37b4b9bf5aad3968/app/src/main/res/raw/tecnicas_chocolateria.mp4"

    @Before fun conectar() = runBlocking {
        org.junit.Assume.assumeTrue("Activar con backendReal=true en los argumentos de instrumentación",
            InstrumentationRegistry.getArguments().getString("backendReal") == "true")
        app.red.guardar("http://127.0.0.1:8080/", true)
        app.usuarios.cerrarSesion()
        assertNull(app.usuarios.registrar("Estudiante Prueba", "ui${System.nanoTime()}@test.cl", "912345678", "Martini2026"))
        app.usuarios.guardarIntereses(setOf("Recetas"))
    }

    private fun visible(matcher: SemanticsMatcher): SemanticsNodeInteraction {
        val nodo = compose.onNode(matcher)
        if (!nodo.isDisplayed()) {
            if (compose.onAllNodes(hasScrollToNodeAction()).fetchSemanticsNodes().isNotEmpty())
                compose.onNode(hasScrollToNodeAction()).performScrollToNode(matcher)
            else nodo.performScrollTo()
        }
        return compose.onNode(matcher)
    }
    private fun pulsar(texto: String) {
        // Una búsqueda puede contener el mismo texto que la tarjeta: pulsar la tarjeta, no el campo.
        val matcher = hasClickAction() and hasText(texto) and !hasSetTextAction()
        visible(matcher)
        compose.waitUntil(30_000) { compose.onAllNodes(matcher and isEnabled()).fetchSemanticsNodes().isNotEmpty() }
        visible(matcher).performClick()
    }
    private fun campo(etiqueta: String, valor: String) =
        visible(hasSetTextAction() and hasText(etiqueta)).performTextReplacement(valor)
    private fun esperar(texto: String, tiempo: Long = 30_000) {
        val normalizar: (String) -> String = { it.replace(Regex("\\s+"), " ").trim() }
        val coincide = SemanticsMatcher("Texto: $texto") { nodo ->
            val textos = if (nodo.config.contains(SemanticsProperties.Text)) nodo.config[SemanticsProperties.Text] else emptyList()
            textos.any { normalizar(it.text) == normalizar(texto) }
        }
        compose.waitUntil(tiempo) { compose.onAllNodes(coincide).fetchSemanticsNodes().isNotEmpty() }
    }
    private fun captura(nombre: String) {
        compose.waitForIdle()
        val imagen = InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot()
        if (Build.VERSION.SDK_INT >= 29) {
            // El runner desinstala la app al terminar. Pictures conserva la evidencia del emulador.
            val valores = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "$nombre.png")
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MasterMartiniPruebas")
            }
            val uri = app.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, valores)!!
            app.contentResolver.openOutputStream(uri)!!.use { imagen.compress(Bitmap.CompressFormat.PNG, 100, it) }
        } else {
            val carpeta = app.getExternalFilesDir("evidencias")!!
            carpeta.mkdirs()
            File(carpeta, "$nombre.png").outputStream().use { imagen.compress(Bitmap.CompressFormat.PNG, 100, it) }
        }
        imagen.recycle()
    }
    private fun abrirModulo(titulo: String) {
        esperar("Aprende algo rico hoy ✨")
        pulsar(titulo)
        pulsar("Gestionar contenidos del servidor")
    }

    private fun crud(tipo: String, titulo: String, detalle: String) {
        val original = "Prueba UI $tipo ${System.currentTimeMillis()}"
        val editado = "$original editado"
        val repo = app.red.repositorio(tipo)
        var id: Long? = null
        try {
            ActivityScenario.launch(MainActivity::class.java).use { activity ->
                abrirModulo(titulo)
                pulsar("Crear $tipo")
                pulsar("Guardar en servidor")
                compose.onAllNodesWithText("Campo obligatorio").onFirst().assertExists()
                campo("Título", original)
                campo("Categoría", "Pruebas de integración")
                campo("Duración (ej. 30 minutos)", "10 minutos")
                campo("Descripción", "Registro temporal creado desde la pantalla Android.")
                campo(detalle, "Ingredientes y pasos de práctica para verificar el CRUD completo.")
                if (tipo == "Video") campo("Enlace directo al video MP4 (HTTPS)", video)
                pulsar("Guardar en servidor")
                esperar("Guardado en el servidor")
                id = runBlocking { repo.listar().single { it.titulo == original }.id }
                assertEquals(original, runBlocking { repo.consultar(id!!) }.titulo)
                captura("${tipo}_creado")

                compose.onNodeWithContentDescription("Volver").performClick()
                // Recrear y actualizar comprueba el retorno al listado y otra consulta real.
                activity.recreate()
                esperar("$titulo · Servidor")
                pulsar("Actualizar")
                campo("Buscar por nombre o categoría", original)
                pulsar(original)
                esperar(original)
                if (tipo == "Video") {
                    compose.waitUntil(60_000) {
                        compose.onAllNodes(hasText("Reproducir") and isEnabled()).fetchSemanticsNodes().isNotEmpty()
                    }
                    pulsar("Reproducir")
                    pulsar("Pausar")
                }
                pulsar("Editar")
                campo("Título", editado)
                pulsar("Guardar en servidor")
                esperar("Guardado en el servidor")
                assertEquals(editado, runBlocking { repo.consultar(id!!) }.titulo)
                captura("${tipo}_editado")
                pulsar("Eliminar")
                esperar("¿Eliminar contenido?")
                compose.onAllNodes(hasText("Eliminar") and hasClickAction()).onLast().performClick()
                esperar("Eliminado del servidor")
                assertFalse(runBlocking { repo.listar() }.any { it.id == id })
                captura("${tipo}_eliminado")
            }
        } finally {
            // Incluso si falla una aserción, no deja el registro temporal en MySQL.
            runBlocking { repo.listar().filter { it.titulo == original || it.titulo == editado }.forEach { repo.eliminar(it.id) } }
        }
    }

    @Test fun cursoCrudDesdeLaInterfaz() = crud("Curso", "Cursos", "Programa y unidades")
    @Test fun recetaCrudDesdeLaInterfaz() = crud("Receta", "Recetas", "Ingredientes y preparación")
    @Test fun videoCrudYReproduccionRemota() = crud("Video", "Videos", "Guía del video")

    @Test fun registroLoginRemotoYRechazoDeClaveIncorrecta() {
        app.usuarios.cerrarSesion()
        val correo = "registro_ui${System.nanoTime()}@test.cl"
        ActivityScenario.launch(MainActivity::class.java).use {
            pulsar("Crear una cuenta")
            campo("Nombre", "Estudiante Remoto")
            campo("Correo electrónico", correo)
            campo("Teléfono", "912345678")
            campo("Contraseña", "Martini2026")
            campo("Repite la contraseña", "Martini2026")
            pulsar("Crear cuenta")
            esperar("Tus intereses")
            pulsar("Recetas")
            pulsar("Guardar y continuar")
            esperar("Aprende algo rico hoy ✨")
            captura("registro_remoto_correcto")
            pulsar("Mi perfil")
            pulsar("Cerrar sesión")
            compose.onAllNodes(hasText("Cerrar sesión") and hasClickAction()).onLast().performClick()
            esperar("Inicia sesión para continuar")
            campo("Correo electrónico", correo)
            campo("Contraseña", "incorrecta")
            pulsar("Iniciar sesión")
            esperar("Correo o contraseña incorrectos")
            campo("Contraseña", "Martini2026")
            pulsar("Iniciar sesión")
            esperar("Aprende algo rico hoy ✨")
        }
    }

    @Test fun apiExternaMuestraResultadosDetalleYBusquedaVacia() {
        val primera = runBlocking { app.red.recetasExternas().buscar("cake").first().strMeal }
        ActivityScenario.launch(MainActivity::class.java).use {
            esperar("Aprende algo rico hoy ✨")
            pulsar("Recetas")
            pulsar("Explorar recetas reales · TheMealDB")
            esperar(primera)
            captura("api_externa_resultados")
            pulsar(primera)
            compose.waitUntil(30_000) {
                compose.onAllNodesWithText("Nombre de receta en inglés").fetchSemanticsNodes().isEmpty()
            }
            esperar(primera)
            captura("api_externa_detalle")
            compose.onNodeWithContentDescription("Volver").performClick()
            campo("Nombre de receta en inglés", "zzmastermartinisinresultadoszz")
            pulsar("Buscar en TheMealDB")
            esperar("No encontramos recetas con ese nombre.")
        }
    }

    @Test fun servidorInaccesibleMuestraErrorSinCerrarLaApp() {
        app.red.guardar("http://127.0.0.1:65534/", true)
        try {
            ActivityScenario.launch(MainActivity::class.java).use {
                abrirModulo("Cursos")
                esperar("No se pudo conectar. Revisa internet, la dirección del servidor y que el backend esté encendido.")
                visible(hasText("Actualizar") and hasClickAction()).assertIsEnabled()
                captura("error_conexion_controlado")
            }
        } finally { app.red.guardar("http://127.0.0.1:8080/", true) }
    }

    @Test fun temporizadorSeConservaAlRecrearLaPantalla() {
        ActivityScenario.launch(MainActivity::class.java).use { activity ->
            esperar("Aprende algo rico hoy ✨")
            pulsar("Recetas")
            campo("Buscar por nombre o categoría", "Brownie")
            pulsar("Ver receta")
            pulsar("Iniciar 15 minutos")
            visible(hasText("Cancelar") and hasClickAction()).assertExists()
            activity.recreate()
            visible(hasText("Cancelar") and hasClickAction()).assertExists().performClick()
            visible(hasText("00:00")).assertExists()
        }
    }

    @Test fun fotoSeReduceYSeGuardaEnElPerfil() {
        val archivo = File(app.cacheDir, "imagen-prueba-${System.nanoTime()}.png")
        val imagen = Bitmap.createBitmap(2200, 1600, Bitmap.Config.ARGB_8888)
        imagen.eraseColor(android.graphics.Color.MAGENTA)
        archivo.outputStream().use { imagen.compress(Bitmap.CompressFormat.PNG, 100, it) }
        imagen.recycle()
        lateinit var vm: PerfilViewModel
        val store = ViewModelStore()
        InstrumentationRegistry.getInstrumentation().runOnMainSync { vm = PerfilViewModel(app); store.put("perfil", vm) }
        try {
            compose.waitUntil(10_000) { vm.estado.value.listo }
            InstrumentationRegistry.getInstrumentation().runOnMainSync { vm.guardarFoto(Uri.fromFile(archivo)) }
            compose.waitUntil(15_000) { !vm.estado.value.cargando && vm.estado.value.foto.isNotBlank() }
            val foto = BitmapFactory.decodeFile(vm.estado.value.foto)
            assertNotNull(foto)
            assertTrue(foto.width <= 1024 && foto.height <= 1024)
            foto.recycle()
            assertEquals(vm.estado.value.foto, runBlocking { app.usuarios.usuario.first() }!!.foto)
        } finally {
            InstrumentationRegistry.getInstrumentation().runOnMainSync { store.clear() }
            archivo.delete()
        }
    }
}
