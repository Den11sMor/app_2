package com.mastermartini.app.ui.viewmodels

import com.mastermartini.app.data.repository.CatalogoRemoto

// Este ViewModel representa videos. Hereda las operaciones comunes para no repetir el CRUD.
// Recibe el repositorio por constructor: en las pruebas se puede reemplazar por uno falso.
class VideosViewModel(repository: CatalogoRemoto) : ModuloViewModel(repository, "Video")
