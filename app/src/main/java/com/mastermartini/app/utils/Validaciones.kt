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

    // Valida que el correo solo contenga letras, números y punto (.) antes del @, y solo letras con punto (.) después del @.
    fun correo(valor: String): String? {
        val texto = valor.trim()
        val regexCorreoStrict = Regex("^[a-zA-Z0-9.]+@[a-zA-Z]+(\\.[a-zA-Z]+)+$")

        return when {
            texto.isBlank() -> "Ingresa tu correo electrónico"
            !regexCorreoStrict.matches(texto) -> "Correo no válido (solo letras, números y punto; sin símbolos como _ % + -)"
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
