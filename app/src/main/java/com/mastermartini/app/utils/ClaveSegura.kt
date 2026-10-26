package com.mastermartini.app.utils

import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object ClaveSegura {
    fun crearSal(): String = ByteArray(16).also { SecureRandom().nextBytes(it) }.aHex()

    fun hash(clave: String, sal: String): String {
        // Android 7 no siempre incluye SHA256 en su proveedor de PBKDF2.
        val algoritmo = try {
            SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            "PBKDF2WithHmacSHA256"
        } catch (_: java.security.NoSuchAlgorithmException) {
            "PBKDF2WithHmacSHA1"
        }
        return "$algoritmo:${derivar(clave, sal, algoritmo)}"
    }

    private fun derivar(clave: String, sal: String, algoritmo: String): String {
        val bytes = sal.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
        val spec = PBEKeySpec(clave.toCharArray(), bytes, 120_000, 256)
        return try {
            SecretKeyFactory.getInstance(algoritmo).generateSecret(spec).encoded.aHex()
        } finally {
            spec.clearPassword()
        }
    }

    fun verificar(clave: String, sal: String, esperado: String): Boolean {
        val partes = esperado.split(":", limit = 2)
        if (partes.size != 2 || partes[0] !in listOf("PBKDF2WithHmacSHA256", "PBKDF2WithHmacSHA1")) return false
        return MessageDigest.isEqual(derivar(clave, sal, partes[0]).toByteArray(), partes[1].toByteArray())
    }

    private fun ByteArray.aHex(): String = joinToString("") { "%02x".format(it) }
}
