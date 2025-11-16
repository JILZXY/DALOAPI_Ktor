package com.example.shared.security

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.http.*
import io.ktor.server.routing.Route
import io.ktor.server.routing.RouteSelectorEvaluation
import io.ktor.server.routing.RoutingResolveContext
import io.ktor.server.routing.intercept
import io.ktor.server.routing.RouteSelector

class RoleBasedAuthorization(private val allowedRoles: Set<Int>) {
    suspend fun authorize(call: ApplicationCall): Boolean {
        val principal = call.principal<JWTPrincipal>()
        val userRole = principal?.payload?.getClaim("rolId")?.asInt()

        return if (userRole in allowedRoles) {
            true
        } else {
            call.respond(HttpStatusCode.Forbidden, mapOf("error" to "No tienes permisos"))
            false
        }
    }
}

fun Route.authorizeRole(vararg allowedRoles: Int, build: Route.() -> Unit): Route {
    // Interceptar la llamada antes de ejecutar la ruta
    intercept(ApplicationCallPipeline.Call) {
        val principal = call.principal<JWTPrincipal>()
        val userRole = principal?.payload?.getClaim("rolId")?.asInt()

        // Si el rol del usuario no está en los roles permitidos, denegar acceso
        if (userRole == null || userRole !in allowedRoles) {
            call.respond(
                HttpStatusCode.Forbidden,
                mapOf("error" to "No tienes permisos para acceder a este recurso")
            )
            finish()
            return@intercept
        }
    }

    // Si pasa la validación, ejecutar las rutas internas
    build()
    return this
}