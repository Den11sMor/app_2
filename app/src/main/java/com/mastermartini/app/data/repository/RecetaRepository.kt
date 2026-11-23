package com.mastermartini.app.data.repository

import com.mastermartini.app.data.remote.RecetaApi

// Selecciona la API de recetas y reutiliza el manejo de errores del repositorio común.
class RecetaRepository(api: RecetaApi) : ContenidoRemotoRepository(api)
