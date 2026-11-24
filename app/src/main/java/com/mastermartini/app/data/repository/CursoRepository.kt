package com.mastermartini.app.data.repository

import com.mastermartini.app.data.remote.CursoApi

// Selecciona la API de cursos y reutiliza el manejo de errores del repositorio común.
class CursoRepository(api: CursoApi) : ContenidoRemotoRepository(api)
