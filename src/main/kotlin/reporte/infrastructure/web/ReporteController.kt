package com.example.reporte.infrastructure.web

import com.example.reporte.domain.model.CreateReporteRequest
import com.example.reporte.domain.port.ReporteServicePort
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class ReporteController(
    private val reporteService: ReporteServicePort
) {

    fun Route.reporteRoutes() {
        route("/api/reportes") {

            authenticate("auth-jwt") {

                // Ver todos los reportes (solo administradores - futuro)
                // Por ahora accesible para todos los autenticados
                get {
                    val reportes = reporteService.getAllReportes()
                    call.respond(HttpStatusCode.OK, reportes)
                }

                get("/{id}") {
                    val id = call.parameters["id"]?.toIntOrNull()

                    if (id == null) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            mapOf("error" to "ID inválido")
                        )
                        return@get
                    }

                    val reporte = reporteService.getReporteById(id)

                    if (reporte != null) {
                        call.respond(HttpStatusCode.OK, reporte)
                    } else {
                        call.respond(
                            HttpStatusCode.NotFound,
                            mapOf("error" to "Reporte no encontrado")
                        )
                    }
                }

                // Ver reportes que he hecho
                get("/mis-reportes") {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.getClaim("userId")?.asString()

                    if (userId == null) {
                        call.respond(
                            HttpStatusCode.Unauthorized,
                            mapOf("error" to "Usuario no identificado")
                        )
                        return@get
                    }

                    val reportes = reporteService.getReportesByUsuarioReportaId(userId)
                    call.respond(HttpStatusCode.OK, reportes)
                }

                // Ver reportes en mi contra
                get("/contra-mi") {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.getClaim("userId")?.asString()

                    if (userId == null) {
                        call.respond(
                            HttpStatusCode.Unauthorized,
                            mapOf("error" to "Usuario no identificado")
                        )
                        return@get
                    }

                    val reportes = reporteService.getReportesByUsuarioReportadoId(userId)
                    call.respond(HttpStatusCode.OK, reportes)
                }

                // Ver reportes de un usuario específico (reportado)
                get("/usuario/{usuarioId}") {
                    val usuarioId = call.parameters["usuarioId"]

                    if (usuarioId == null) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            mapOf("error" to "ID de usuario inválido")
                        )
                        return@get
                    }

                    val reportes = reporteService.getReportesByUsuarioReportadoId(usuarioId)
                    call.respond(HttpStatusCode.OK, reportes)
                }

                // Crear un reporte (cualquier usuario autenticado puede reportar)
                post {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.getClaim("userId")?.asString()

                    if (userId == null) {
                        call.respond(
                            HttpStatusCode.Unauthorized,
                            mapOf("error" to "Usuario no identificado")
                        )
                        return@post
                    }

                    val request = call.receive<CreateReporteRequest>()
                    val reporte = reporteService.createReporte(userId, request)

                    if (reporte != null) {
                        call.respond(HttpStatusCode.Created, reporte)
                    } else {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            mapOf("error" to "No se pudo crear el reporte. No puedes reportarte a ti mismo.")
                        )
                    }
                }

                // Eliminar un reporte (solo quien lo creó o admin)
                delete("/{id}") {
                    val id = call.parameters["id"]?.toIntOrNull()
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.getClaim("userId")?.asString()

                    if (id == null) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            mapOf("error" to "ID inválido")
                        )
                        return@delete
                    }

                    if (userId == null) {
                        call.respond(
                            HttpStatusCode.Unauthorized,
                            mapOf("error" to "Usuario no identificado")
                        )
                        return@delete
                    }

                    // Verificar que el reporte le pertenece
                    val reporte = reporteService.getReporteById(id)
                    if (reporte == null) {
                        call.respond(
                            HttpStatusCode.NotFound,
                            mapOf("error" to "Reporte no encontrado")
                        )
                        return@delete
                    }

                    if (reporte.usuarioReportaId != userId) {
                        call.respond(
                            HttpStatusCode.Forbidden,
                            mapOf("error" to "No puedes eliminar este reporte")
                        )
                        return@delete
                    }

                    val deleted = reporteService.deleteReporte(id)

                    if (deleted) {
                        call.respond(
                            HttpStatusCode.OK,
                            mapOf("message" to "Reporte eliminado")
                        )
                    } else {
                        call.respond(
                            HttpStatusCode.NotFound,
                            mapOf("error" to "Reporte no encontrado")
                        )
                    }
                }
            }
        }

        // Rutas para motivos de reporte (catálogo)
        route("/api/motivos-reporte") {
            // Público - Ver todos los motivos disponibles
            get {
                val motivos = reporteService.getAllMotivosReporte()
                call.respond(HttpStatusCode.OK, motivos)
            }
        }
    }
}