package com.mastermartini.app

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavegacionTest {
    @get:Rule val compose = createEmptyComposeRule()

    private fun mostrar(matcher: SemanticsMatcher): SemanticsNodeInteraction {
        val nodo = compose.onNode(matcher)
        if (!nodo.isDisplayed()) {
            if (compose.onAllNodes(matcher).fetchSemanticsNodes().isNotEmpty()) nodo.performScrollTo()
            else compose.onNode(hasScrollToNodeAction() and
                SemanticsMatcher.keyIsDefined(SemanticsProperties.VerticalScrollAxisRange)).performScrollToNode(matcher)
        }
        return compose.onNode(matcher)
    }
    private fun pulsar(texto: String) {
        val matcher = hasClickAction() and hasText(texto) and !hasSetTextAction()
        mostrar(matcher)
        compose.waitUntil(30_000) { compose.onAllNodes(matcher and isEnabled()).fetchSemanticsNodes().isNotEmpty() }
        mostrar(matcher).performClick()
    }
    private fun campo(etiqueta: String, valor: String) =
        mostrar(hasSetTextAction() and hasText(etiqueta)).performTextInput(valor)
    private fun barra() {
        compose.onAllNodesWithTag("barra-inferior").assertCountEquals(1)
        listOf("home", "favoritos", "perfil").forEach {
            compose.onNodeWithTag("nav-$it").assertIsDisplayed()
        }
    }

    @Test fun barraPermaneceEnCatalogosDetallesFormulariosYPerfil() {
        val app = ApplicationProvider.getApplicationContext<MasterMartiniApplication>()
        app.red.guardar(app.red.url, false)
        app.usuarios.cerrarSesion()
        ActivityScenario.launch(MainActivity::class.java).use {
            compose.onNodeWithTag("barra-inferior").assertDoesNotExist()
            pulsar("Crear una cuenta")
            compose.onNodeWithTag("barra-inferior").assertDoesNotExist()
            campo("Nombre", "Estudiante Navegacion")
            campo("Correo electrónico", "navegacion_${System.currentTimeMillis()}@test.cl")
            campo("Teléfono", "912345678")
            campo("Contraseña", "Martini2026")
            campo("Repite la contraseña", "Martini2026")
            pulsar("Crear cuenta")
            compose.waitUntil(15_000) { compose.onAllNodesWithText("Tus intereses").fetchSemanticsNodes().isNotEmpty() }
            compose.onNodeWithTag("barra-inferior").assertDoesNotExist()
            pulsar("Recetas")
            pulsar("Guardar y continuar")
            compose.waitUntil(15_000) { compose.onAllNodesWithTag("barra-inferior").fetchSemanticsNodes().isNotEmpty() }
            barra()
            for ((catalogo,tipo) in listOf("Recetas" to "Receta", "Videos" to "Video", "Cursos" to "Curso")) {
                compose.onNodeWithTag("nav-home").performClick()
                compose.waitUntil(15_000) { compose.onAllNodesWithText("Aprende algo rico hoy ✨").fetchSemanticsNodes().isNotEmpty() }
                pulsar(catalogo)
                barra()
                if (tipo == "Receta") {
                    compose.onAllNodesWithText("Ver receta").onFirst().performScrollTo().performClick()
                    barra()
                    compose.onNodeWithContentDescription("Volver").performClick()
                    pulsar("Explorar recetas reales · TheMealDB")
                    barra()
                    compose.onNodeWithContentDescription("Volver").performClick()
                }
                pulsar("Gestionar contenidos del servidor")
                barra()
                pulsar("Crear $tipo")
                barra()
                // Inicio permite salir también desde el formulario, sin guardar un registro vacío.
            }
            compose.onNodeWithTag("nav-favoritos").performClick()
            barra()
            compose.onNodeWithTag("nav-favoritos").assertIsSelected()
            compose.onNodeWithTag("nav-perfil").performClick()
            barra()
            compose.onNodeWithTag("nav-perfil").assertIsSelected()
            pulsar("Cambiar intereses")
            barra()
            compose.onNodeWithTag("nav-perfil").assertIsSelected()
            compose.onNodeWithTag("nav-perfil").performClick()
            pulsar("Cerrar sesión")
            compose.onAllNodes(hasText("Cerrar sesión") and hasClickAction()).onLast().performClick()
            compose.waitUntil(15_000) {
                compose.onAllNodes(hasSetTextAction() and hasText("Correo electrónico")).fetchSemanticsNodes().isNotEmpty()
            }
            compose.onNodeWithTag("barra-inferior").assertDoesNotExist()
        }
    }
}
