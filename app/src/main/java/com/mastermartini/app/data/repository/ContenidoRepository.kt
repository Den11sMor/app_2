package com.mastermartini.app.data.repository

import com.mastermartini.app.data.local.dao.ContenidoDao
import com.mastermartini.app.data.local.entity.ContenidoEntity
import com.mastermartini.app.domain.model.*
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class ContenidoRepository(private val dao: ContenidoDao) {
    val contenidos = dao.observarTodos().onStart { dao.insertarTodos(ejemplos) }.map { filas ->
        filas.map { fila ->
            with(fila) {
                when (tipo) {
                    "Receta" -> Receta(id, titulo, categoria, duracion, descripcion, detalle)
                    "Video" -> Video(id, titulo, categoria, duracion, descripcion, detalle)
                    else -> Curso(id, titulo, categoria, duracion, descripcion, detalle)
                }
            }
        }
    }

    // Catálogo educativo local; conserva los contenidos de las primeras pantallas.
    private val ejemplos = listOf(
        ContenidoEntity(1, "Torta de chocolate", "Receta", "Pastelería", "45 minutos",
            "Una preparación para compartir y practicar mezclas básicas.",
            "Ingredientes\n250 g de harina\n200 g de chocolate\n150 g de azúcar\n3 huevos\n150 ml de leche\n100 g de mantequilla\n10 g de polvo de hornear\n\nPreparación\n1. Precalienta el horno a 180 °C y engrasa un molde.\n2. Derrite el chocolate con la mantequilla.\n3. Bate huevos y azúcar. Agrega leche y chocolate tibio.\n4. Incorpora harina y polvo de hornear tamizados.\n5. Hornea entre 30 y 35 minutos y comprueba con un palillo. Deja enfriar."),
        ContenidoEntity(2, "Brownie", "Receta", "Chocolatería", "35 minutos",
            "Chocolate intenso y una textura húmeda.",
            "Ingredientes\n200 g de chocolate\n120 g de mantequilla\n150 g de azúcar\n2 huevos\n80 g de harina\n\nPreparación\n1. Precalienta el horno a 180 °C.\n2. Derrite mantequilla y chocolate.\n3. Bate huevos con azúcar y agrega la mezcla tibia.\n4. Incorpora harina sin batir de más.\n5. Hornea entre 20 y 25 minutos. Deja enfriar antes de cortar."),
        ContenidoEntity(3, "Croissant", "Receta", "Bollería", "60 minutos + reposo",
            "Una introducción al laminado con mantequilla.",
            "Ingredientes\n500 g de harina\n250 ml de leche\n50 g de azúcar\n10 g de levadura seca\n10 g de sal\n250 g de mantequilla para laminar\n1 huevo para pintar\n\nPreparación\n1. Amasa harina, leche, azúcar, levadura y sal. Refrigera una hora.\n2. Encierra la mantequilla fría dentro de la masa.\n3. Estira y realiza tres pliegues simples; enfría 30 minutos entre cada uno.\n4. Corta triángulos y enrolla. Deja fermentar hasta aumentar su volumen.\n5. Pinta con huevo y hornea a 190 °C entre 18 y 22 minutos."),
        ContenidoEntity(4, "Técnicas de chocolatería", "Video", "Chocolatería", "12 minutos",
            "Guía de estudio: templado, moldes y conservación.",
            "Guía de estudio\n1. Identifica los tipos de chocolate.\n2. Revisa la temperatura recomendada por el fabricante.\n3. Practica el templado en una superficie limpia y seca.\n4. Usa moldes secos y evita el contacto con agua.\n\nEste prototipo contiene una guía local. El archivo de video aún no está incluido."),
        ContenidoEntity(5, "Decoración de tortas", "Video", "Pastelería", "8 minutos",
            "Guía de estudio para practicar con manga pastelera.",
            "Guía de estudio\n1. Nivela y enfría la torta.\n2. Aplica una capa fina de cobertura.\n3. Practica líneas sobre papel antes de decorar.\n4. Prueba boquillas de estrella y redondas.\n\nEste prototipo contiene una guía local. El archivo de video aún no está incluido."),
        ContenidoEntity(6, "Pastelería básica", "Curso", "Principiante", "4 horas",
            "Un recorrido práctico por las bases de la pastelería.",
            "Programa de estudio\nUnidad 1. Higiene, utensilios y medidas.\nUnidad 2. Batidos y bizcochos.\nUnidad 3. Cremas y rellenos.\nUnidad 4. Montaje y decoración.\n\nActividad sugerida\nPrepara la torta de chocolate del catálogo y registra el resultado en tu foto de perfil.\n\nContenido educativo de demostración; no incluye inscripción ni certificación."),
        ContenidoEntity(7, "Introducción a la chocolatería", "Curso", "Principiante", "3 horas",
            "Conoce el chocolate y sus aplicaciones.",
            "Programa de estudio\nUnidad 1. Cacao y variedades de chocolate.\nUnidad 2. Fundido y templado.\nUnidad 3. Moldes y rellenos.\n\nActividad sugerida\nCompara el resultado del brownie usando distintos porcentajes de cacao.\n\nContenido educativo de demostración; no incluye inscripción ni certificación.")
    )
}
