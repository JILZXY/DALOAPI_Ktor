package com.example

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.shared.config.DependencyInjection
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureSecurity() {
    val jwtConfig = DependencyInjection.jwtConfig

    install(Authentication) {
        jwt("auth-jwt") {
            verifier(jwtConfig.getVerifier())

            validate { credential ->
                if (credential.payload.getClaim("userId").asString() != null) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }

            challenge { _, _ ->
                call.respond(
                    io.ktor.http.HttpStatusCode.Unauthorized,
                    mapOf("error" to "Token inválido o expirado")
                )
            }
        }
    }
}
