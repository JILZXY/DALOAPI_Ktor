package com.example.calificacion.infrastructure.web

import com.example.calificacion.domain.model.CreateCalificacionRequest
import com.example.calificacion.domain.port.Service.CalificacionServicePort
import com.example.shared.security.authorizeRole
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class CalificacionController(
    private val calificacionService: CalificacionServicePort
) {

    fun Route.calificacionRoutes() {
        route("/api/calificaciones") {

            // Públicas - Ver calificaciones de un abogado
            get("/abogado/{abogadoId}") {
                val abogadoId = call.parameters["abogadoId"]

                if (abogadoId == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID de abogado inválido"))
                    return@get
                }

                val calificaciones = calificacionService.getCalificacionesByAbogadoId(abogadoId)
                call.respond(HttpStatusCode.OK, calificaciones)
            }

            // Ver promedios de un abogado
            get("/abogado/{abogadoId}/promedios") {
                val abogadoId = call.parameters["abogadoId"]

                if (abogadoId == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID de abogado inválido"))
                    return@get
                }

                val promedios = calificacionService.getPromediosByAbogado(abogadoId)
                call.respond(HttpStatusCode.OK, promedios)
            }

            get("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()

                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID inválido"))
                    return@get
                }

                val calificacion = calificacionService.getCalificacionById(id)

                if (calificacion != null) {
                    call.respond(HttpStatusCode.OK, calificacion)
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Calificación no encontrada"))
                }
            }

            // Obtener calificación promedio general de un abogado
            get("/promedio/{id}") {
                val id = call.parameters["id"]

                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID inválido"))
                    return@get
                }

                val promedioGeneral = calificacionService.getPromedioGeneralAbogado(id)
                call.respond(HttpStatusCode.OK, mapOf("promedioGeneral" to promedioGeneral))
            }

            // Obtener promedios detallados por criterio de una respuesta específica
            get("/promedios/respuesta/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()

                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID inválido"))
                    return@get
                }

                val promedios = calificacionService.getPromediosPorRespuesta(id)
                call.respond(HttpStatusCode.OK, promedios)
            }

            // Protegidas - Solo clientes pueden calificar abogados
            authenticate("auth-jwt") {
                authorizeRole(1) {
                    post("/abogado/{abogadoId}") {
                        val abogadoId = call.parameters["abogadoId"]
                        val principal = call.principal<JWTPrincipal>()
                        val userId = principal?.payload?.getClaim("userId")?.asString()

                        if (abogadoId == null) {
                            call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID de abogado inválido"))
                            return@post
                        }

                        if (userId == null) {
                            call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Usuario no identificado"))
                            return@post
                        }

                        val request = call.receive<CreateCalificacionRequest>()
                        val calificacion = calificacionService.createCalificacion(userId, abogadoId, request)

                        if (calificacion != null) {
                            call.respond(HttpStatusCode.Created, calificacion)
                        } else {
                            call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Error al crear calificación. Verifica que los valores estén entre 1 y 5"))
                        }
                    }
                }
            }
        }
    }
}