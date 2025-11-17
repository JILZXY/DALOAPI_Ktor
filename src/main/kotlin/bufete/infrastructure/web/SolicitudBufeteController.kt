package com.example.bufete.infrastructure.web

import com.example.bufete.domain.model.CreateSolicitudBufeteRequest
import com.example.bufete.domain.model.UpdateSolicitudEstadoRequest
import com.example.bufete.domain.port.SolicitudBufeteServicePort
import com.example.bufete.domain.port.BufeteServicePort
import com.example.shared.security.authorizeRole
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class SolicitudBufeteController(
    private val solicitudService: SolicitudBufeteServicePort,
    private val bufeteService: BufeteServicePort
) {

    fun Route.solicitudBufeteRoutes() {
        route("/api/solicitudes-bufete") {

            authenticate("auth-jwt") {

                // Solo abogados pueden solicitar unirse
                authorizeRole(2) {
                    post {
                        val principal = call.principal<JWTPrincipal>()
                        val abogadoId = principal?.payload?.getClaim("userId")?.asString()

                        if (abogadoId == null) {
                            call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Abogado no identificado"))
                            return@post
                        }

                        val request = call.receive<CreateSolicitudBufeteRequest>()
                        val solicitud = solicitudService.createSolicitud(abogadoId, request.bufeteId)

                        if (solicitud != null) {
                            call.respond(HttpStatusCode.Created, solicitud)
                        } else {
                            call.respond(HttpStatusCode.BadRequest, mapOf("error" to "No se pudo crear la solicitud"))
                        }
                    }

                    get("/mis-solicitudes") {
                        val principal = call.principal<JWTPrincipal>()
                        val abogadoId = principal?.payload?.getClaim("userId")?.asString()

                        if (abogadoId == null) {
                            call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Abogado no identificado"))
                            return@get
                        }

                        val solicitudes = solicitudService.getSolicitudesByAbogadoId(abogadoId)
                        call.respond(HttpStatusCode.OK, solicitudes)
                    }
                }

                // Ver solicitudes de un bufete (solo admin del bufete)
                get("/bufete/{bufeteId}") {
                    val bufeteId = call.parameters["bufeteId"]?.toIntOrNull()
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.getClaim("userId")?.asString()

                    if (bufeteId == null) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID de bufete inválido"))
                        return@get
                    }

                    if (userId == null) {
                        call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Usuario no identificado"))
                        return@get
                    }

                    // Verificar que es admin del bufete
                    val bufete = bufeteService.getBufeteById(bufeteId)
                    if (bufete == null) {
                        call.respond(HttpStatusCode.NotFound, mapOf("error" to "Bufete no encontrado"))
                        return@get
                    }

                    if (bufete.adminBufeteId != userId) {
                        call.respond(HttpStatusCode.Forbidden, mapOf("error" to "No tienes acceso a estas solicitudes"))
                        return@get
                    }

                    val solicitudes = solicitudService.getSolicitudesByBufeteId(bufeteId)
                    call.respond(HttpStatusCode.OK, solicitudes)
                }

                // Aprobar/Rechazar solicitud (solo admin del bufete)
                patch("/{id}") {
                    val id = call.parameters["id"]?.toIntOrNull()
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.getClaim("userId")?.asString()

                    if (id == null) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID inválido"))
                        return@patch
                    }

                    if (userId == null) {
                        call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Usuario no identificado"))
                        return@patch
                    }

                    // Verificar que es admin del bufete
                    val solicitud = solicitudService.getSolicitudById(id)
                    if (solicitud == null) {
                        call.respond(HttpStatusCode.NotFound, mapOf("error" to "Solicitud no encontrada"))
                        return@patch
                    }

                    val bufete = bufeteService.getBufeteById(solicitud.bufeteId)
                    if (bufete == null || bufete.adminBufeteId != userId) {
                        call.respond(HttpStatusCode.Forbidden, mapOf("error" to "No puedes modificar esta solicitud"))
                        return@patch
                    }

                    val request = call.receive<UpdateSolicitudEstadoRequest>()

                    val resultado = when (request.estado) {
                        "Aprobado" -> solicitudService.aprobarSolicitud(id)
                        "Rechazado" -> solicitudService.rechazarSolicitud(id)
                        else -> false
                    }

                    if (resultado) {
                        call.respond(HttpStatusCode.OK, mapOf("message" to "Solicitud actualizada"))
                    } else {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Estado inválido"))
                    }
                }
            }
        }
    }
}