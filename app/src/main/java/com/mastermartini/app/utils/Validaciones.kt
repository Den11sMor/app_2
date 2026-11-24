package com.mastermartini.app.utils

// Contiene las reglas de validación para los formularios de la aplicación.
object Validaciones {

    // Valida que el nombre contenga solo letras y formato de Nombre y Apellido.
    fun nombre(valor: String): String? {
        val texto = valor.trim()
        val regexSoloLetrasYEspacios = Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")

        return when {
            texto.isBlank() -> "Ingresa tu nombre y apellido"
            texto.length < 3 -> "Ingresa al menos 3 caracteres"
            texto.length > 60 -> "Usa hasta 60 caracteres"
            !regexSoloLetrasYEspacios.matches(texto) -> "Solo se permiten letras (sin números ni símbolos)"
            !texto.contains(" ") || texto.split("\\s+".toRegex()).size < 2 -> "Ingresa tu nombre y apellido (ej: Juan Pérez)"
            else -> null
        }
    }

    // Acepta direcciones habituales con guion bajo, guion y +; rechaza espacios y partes vacías.
    fun correo(valor: String): String? {
        val texto = valor.trim()
        val formato = Regex("^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        val nombre = texto.substringBefore('@')
        val dominio = texto.substringAfter('@', "").split('.')
        val partesValidas = nombre.length <= 64 && !nombre.startsWith('.') &&
            !nombre.endsWith('.') && !nombre.contains("..") &&
            dominio.all { it.isNotEmpty() && it.length <= 63 && !it.startsWith('-') && !it.endsWith('-') }

        return when {
            texto.isBlank() -> "Ingresa tu correo electrónico"
            texto.length > 254 || !formato.matches(texto) || !partesValidas -> "Ingresa un correo válido, por ejemplo nombre_apellido@correo.com"
            else -> null
        }
    }

    // Valida que el teléfono contenga solo números, con opcional '+' y 2 dígitos del país al inicio.
    fun telefono(valor: String): String? {
        val texto = valor.trim()
        val regexTelefono = Regex("^(?:\\+[0-9]{2})?[0-9]{8,9}$")

        return when {
            texto.isBlank() -> "Ingresa tu teléfono"
            texto.any { it.isLetter() } -> "Ingresa solo números (sin letras)"
            !regexTelefono.matches(texto) -> "Ingresa un número válido (+56 opcional y 8 o 9 dígitos)"
            else -> null
        }
    }

    // Valida que la contraseña tenga al menos 8 caracteres con letras y números.
    fun clave(valor: String): String? = when {
        valor.length < 8 -> "Usa al menos 8 caracteres"
        valor.length > 128 -> "Usa hasta 128 caracteres"
        valor.none { it.isLetter() } || valor.none { it.isDigit() } -> "Incluye letras y números"
        else -> null
    }

    // Valida que la confirmación de la contraseña sea idéntica.
    fun confirmacion(clave: String, repetida: String): String? =
        if (clave == repetida) null else "Las contraseñas no coinciden"
}
