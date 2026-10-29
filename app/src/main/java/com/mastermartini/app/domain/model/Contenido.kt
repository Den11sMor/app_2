package com.mastermartini.app.domain.model

import android.content.Context

// Las tarjetas trabajan con Contenido sin depender de un tipo específico.
abstract class Contenido(
    val id: Int,
    val titulo: String,
    val categoria: String,
    val duracion: String,
    val descripcion: String,
    val detalle: String
) {
    abstract val tipo: String
    abstract fun accion(): String

    // Obtiene el ID del recurso drawable si la imagen existe en la carpeta res/drawable
    fun obtenerImagenResId(context: Context): Int? {
        val nombre = when (id) {
            1 -> "torta_chocolate"
            2 -> "brownie"
            3 -> "croissant"
            4 -> "chocolateria"
            5 -> "decoracion_tortas"
            6 -> "pasteleria_basica"
            7 -> "intro_chocolateria"
            else -> null
        } ?: return null

        val resId = context.resources.getIdentifier(nombre, "drawable", context.packageName)
        return if (resId != 0) resId else null
    }

    // Obtiene el ID del recurso de video en la carpeta res/raw/ si existe
    fun obtenerVideoResId(context: Context): Int? {
        val nombre = when (id) {
            4 -> "tecnicas_chocolateria"
            5 -> "decoracion_tortas"
            else -> null
        } ?: return null

        val resId = context.resources.getIdentifier(nombre, "raw", context.packageName)
        return if (resId != 0) resId else null
    }
}
