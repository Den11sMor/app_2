package com.mastermartini.app.ui.viewmodels

import com.mastermartini.app.data.repository.CatalogoRemoto

// Este ViewModel representa cursos. Hereda las operaciones comunes para no repetir el CRUD.
// Recibe el repositorio por constructor: en las pruebas se puede reemplazar por uno falso.
class CursosViewModel(repository: CatalogoRemoto) : ModuloViewModel(repository, "Curso")
