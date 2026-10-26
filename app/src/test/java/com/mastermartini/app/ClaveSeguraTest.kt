package com.mastermartini.app

import com.mastermartini.app.utils.ClaveSegura
import org.junit.Assert.*
import org.junit.Test

class ClaveSeguraTest {
    @Test fun noGuardaLaClaveOriginalYRechazaUnaClaveDistinta() {
        val sal = ClaveSegura.crearSal()
        val hash = ClaveSegura.hash("Martini2026", sal)
        assertNotEquals("Martini2026", hash)
        assertTrue(ClaveSegura.verificar("Martini2026", sal, hash))
        assertFalse(ClaveSegura.verificar("incorrecta", sal, hash))
    }

    @Test fun dosUsuariosConLaMismaClaveTienenHashesDistintos() {
        val sal1 = ClaveSegura.crearSal()
        val sal2 = ClaveSegura.crearSal()
        assertNotEquals(sal1, sal2)
        assertNotEquals(ClaveSegura.hash("Martini2026", sal1), ClaveSegura.hash("Martini2026", sal2))
    }
}
