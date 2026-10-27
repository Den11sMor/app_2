package com.mastermartini.app.utils

// Se comparten entre registro, login y perfil; no dependen de Compose.
object Validaciones {
    fun nombre(valor: String): String? = when {
        valor.trim().length < 3 -> "Ingresa al menos 3 caracteres"
        valor.trim().length > 60 -> "Usa hasta 60 caracteres"
        else -> null
    }

    fun correo(valor: String): String? =
        if (Regex("^[A-Za-z0-9.!#$%&'*+/=?^_`{|}~-]+@[A-Za-z0-9-]+(?:\\.[A-Za-z0-9-]+)+$")
                .matches(valor.trim())) null else "Ingresa un correo válido"

    fun telefono(valor: String): String? =
        if (Regex("^\\+?[0-9]{9,15}$").matches(valor.trim())) null
        else "Ingresa de 9 a 15 dígitos; el + es opcional"

    fun clave(valor: String): String? = when {
        valor.length < 8 -> "Usa al menos 8 caracteres"
        valor.length > 128 -> "Usa hasta 128 caracteres"
        valor.none { it.isLetter() } || valor.none { it.isDigit() } -> "Incluye letras y números"
        else -> null
    }

    fun confirmacion(clave: String, repetida: String): String? =
        if (clave == repetida) null else "Las contraseñas no coinciden"
}
