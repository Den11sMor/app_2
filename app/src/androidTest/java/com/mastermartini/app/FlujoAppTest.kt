package com.mastermartini.app

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FlujoAppTest {
    @get:Rule val compose = createEmptyComposeRule()

    private fun campo(etiqueta: String, valor: String) {
        compose.onNode(hasSetTextAction() and hasText(etiqueta)).performScrollTo().performTextInput(valor)
    }

    private fun pulsar(texto: String) {
        compose.onNode(hasClickAction() and hasText(texto)).performScrollTo().performClick()
    }

    private fun esperar(texto: String) {
        compose.waitUntil(15000) { compose.onAllNodesWithText(texto).fetchSemanticsNodes().isNotEmpty() }
    }

    @Test fun registroFavoritosPerfilYSesionFuncionan() {
        val app = ApplicationProvider.getApplicationContext<MasterMartiniApplication>()
        app.usuarios.cerrarSesion()
        ActivityScenario.launch(MainActivity::class.java).use { activity ->
            pulsar("Crear una cuenta")
            pulsar("Crear cuenta")
            compose.onNodeWithText("Ingresa al menos 3 caracteres").assertExists()

            val correo = "prueba${System.currentTimeMillis()}@test.cl"
            campo("Nombre", "Estudiante")
            campo("Correo electrónico", correo)
            campo("Teléfono", "912345678")
            campo("Contraseña", "Martini2026")
            campo("Repite la contraseña", "Martini2026")
            pulsar("Crear cuenta")
            esperar("Tus intereses")
            pulsar("Guardar y continuar")
            compose.onNodeWithText("Selecciona al menos un interés").assertExists()
            pulsar("Recetas")
            pulsar("Guardar y continuar")
            esperar("Bienvenido, Estudiante")

            pulsar("Recetas")
            campo("Buscar por nombre o categoría", "Brownie")
            compose.onNodeWithContentDescription("Agregar a favoritos").performScrollTo().performClick()
            compose.waitUntil(5000) {
                compose.onAllNodesWithContentDescription("Quitar de favoritos").fetchSemanticsNodes().isNotEmpty()
            }
            pulsar("Ver receta")
            esperar("Brownie")
            compose.onNodeWithText("Ingredientes", substring = true).assertExists()
            compose.onNodeWithContentDescription("Volver").performClick()
            compose.onNodeWithContentDescription("Volver").performClick()

            pulsar("Mis favoritos")
            esperar("Brownie")
            compose.onNodeWithText("Brownie").assertExists()
            compose.onNodeWithContentDescription("Volver").performClick()
            pulsar("Mi perfil")
            esperar("Teléfono")
            compose.onNode(hasSetTextAction() and hasText("Nombre")).performScrollTo().performTextReplacement("Luis Prueba")
            pulsar("Guardar cambios")
            esperar("Perfil actualizado")
            compose.onNodeWithContentDescription("Volver").performClick()
            esperar("Bienvenido, Luis Prueba")
            activity.recreate()
            esperar("Bienvenido, Luis Prueba")

            pulsar("Mi perfil")
            pulsar("Cerrar sesión")
            compose.onAllNodes(hasText("Cerrar sesión") and hasClickAction()).onLast().performClick()
            esperar("Inicia sesión para continuar")
            campo("Correo electrónico", correo)
            campo("Contraseña", "incorrecta")
            pulsar("Iniciar sesión")
            esperar("Correo o contraseña incorrectos")
            compose.onNode(hasSetTextAction() and hasText("Contraseña")).performTextReplacement("Martini2026")
            pulsar("Iniciar sesión")
            esperar("Bienvenido, Luis Prueba")
        }
    }
}
