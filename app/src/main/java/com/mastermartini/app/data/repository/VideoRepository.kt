package com.mastermartini.app.data.repository

import com.mastermartini.app.data.remote.VideoApi

// Selecciona la API de videos y reutiliza el manejo de errores del repositorio común.
class VideoRepository(api: VideoApi) : ContenidoRemotoRepository(api)
