package com.mastermartini.app.data.repository

import com.mastermartini.app.data.remote.*

interface RecetasExternas {
    suspend fun buscar(nombre: String): List<RecetaExterna>

    suspend fun consultar(id: String): RecetaExterna?
}

// TheMealDB devuelve meals=null cuando no encuentra recetas. Se transforma en una lista vacía.
class RecetaExternaRepository(private val api: RecetaExternaApi) : RecetasExternas {
    override suspend fun buscar(nombre: String) = api.buscar(nombre.trim()).meals.orEmpty()

    override suspend fun consultar(id: String) = api.consultar(id).meals?.firstOrNull()
}
