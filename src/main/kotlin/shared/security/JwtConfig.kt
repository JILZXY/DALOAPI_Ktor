package com.example.shared.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

class JwtConfig {
    private val secret = System.getenv("JWT_SECRET") ?: "your-secret-key-change-this-in-production"
    private val issuer = "legalapp"
    private val audience = "legalapp-users"
    private val validityInMs = 36_000_00 * 24 * 7 // 7 días

    fun generateToken(userId: String, email: String, rolId: Int): String {
        return JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("userId", userId)
            .withClaim("email", email)
            .withClaim("rolId", rolId)
            .withExpiresAt(Date(System.currentTimeMillis() + validityInMs))
            .sign(Algorithm.HMAC256(secret))
    }

    fun getVerifier() = JWT
        .require(Algorithm.HMAC256(secret))
        .withAudience(audience)
        .withIssuer(issuer)
        .build()
}
