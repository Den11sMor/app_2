package com.mastermartini.app

import com.mastermartini.app.utils.Validaciones
import org.junit.Assert.*
import org.junit.Test

class ValidacionesTest {
    @Test fun registroRechazaCamposVacios() {
        assertNotNull(Validaciones.nombre("  "))
        assertNotNull(Validaciones.correo(""))
        assertNotNull(Validaciones.telefono(""))
        assertNotNull(Validaciones.clave(""))
    }

    @Test fun correoAceptaEspaciosExternosPeroNoDireccionesIncompletas() {
        assertNull(Validaciones.correo(" Luis.Cid+app@duoc.cl "))
        listOf("luis", "luis@", "@duoc.cl", "luis@duoc", "luis cid@duoc.cl").forEach {
            assertNotNull(it, Validaciones.correo(it))
        }
    }

    @Test fun telefonoValidaLongitudYCaracteres() {
        assertNull(Validaciones.telefono("+56912345678"))
        assertNull(Validaciones.telefono("912345678"))
        listOf("123", "1234567890123456", "abcdefghi", "++56912345678").forEach {
            assertNotNull(it, Validaciones.telefono(it))
        }
    }

    @Test fun claveExigeLongitudLetrasNumerosYConfirmacion() {
        assertNull(Validaciones.clave("Martini2026"))
        listOf("abc123", "abcdefgh", "12345678", "A1".repeat(65)).forEach {
            assertNotNull(Validaciones.clave(it))
        }
        assertNull(Validaciones.confirmacion("Martini2026", "Martini2026"))
        assertNotNull(Validaciones.confirmacion("Martini2026", "Otra2026"))
    }
}
