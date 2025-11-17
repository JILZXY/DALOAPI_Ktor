package com.example.bufete.infrastructure.web

import com.example.bufete.domain.model.CreateCalificacionBufeteRequest
import com.example.bufete.domain.port.Service.CalificacionBufeteServicePort
import com.example.shared.security.authorizeRole
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class CalificacionBufeteController(
    private val calificacionBufeteService: CalificacionBufeteServicePort
) {

    fun Route.calificacionBufeteRoutes() {
        route("/api/calificaciones-bufete") {

            // Públicas - Ver calificaciones de un bufete
            get("/bufete/{bufeteId}") {
                val bufeteId = call.parameters["bufeteId"]?.toIntOrNull()

                if (bufeteId == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID de bufete inválido"))
                    return@get
                }

                val calificaciones = calificacionBufeteService.getCalificacionesByBufeteId(bufeteId)
                call.respond(HttpStatusCode.OK, calificaciones)
            }

            // Protegidas - Solo clientes pueden calificar bufetes
            authenticate("auth-jwt") {
                authorizeRole(1) {
                    post("/bufete/{bufeteId}") {
                        val bufeteId = call.parameters["bufeteId"]?.toIntOrNull()
                        val principal = call.principal<JWTPrincipal>()
                        val userId = principal?.payload?.getClaim("userId")?.asString()

                        if (bufeteId == null) {
                            call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID de bufete inválido"))
                            return@post
                        }

                        if (userId == null) {
                            call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Usuario no identificado"))
                            return@post
                        }

                        val request = call.receive<CreateCalificacionBufeteRequest>()
                        val calificacion = calificacionBufeteService.createCalificacion(userId, bufeteId, request)

                        if (calificacion != null) {
                            call.respond(HttpStatusCode.Created, calificacion)
                        } else {
                            call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Error al crear calificación"))
                        }
                    }
                }
            }
        }
    }
}