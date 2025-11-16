package com.example.shared.security

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

class PasswordHasher {

    fun hash(password: String): String {
        val salt = generateSalt()
        val hash = hashPassword(password, salt)
        return "$salt:$hash"
    }

    fun verify(password: String, storedHash: String): Boolean {
        val parts = storedHash.split(":")
        if (parts.size != 2) return false

        val salt = parts[0]
        val hash = parts[1]

        val passwordHash = hashPassword(password, salt)
        return passwordHash == hash
    }

    private fun generateSalt(): String {
        val random = SecureRandom()
        val salt = ByteArray(16)
        random.nextBytes(salt)
        return Base64.getEncoder().encodeToString(salt)
    }

    private fun hashPassword(password: String, salt: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        md.update(Base64.getDecoder().decode(salt))
        val hash = md.digest(password.toByteArray())
        return Base64.getEncoder().encodeToString(hash)
    }
}