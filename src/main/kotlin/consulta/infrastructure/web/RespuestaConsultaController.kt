package com.example.consulta.infrastructure.web

import com.example.consulta.domain.model.CreateRespuestaRequest
import com.example.consulta.domain.port.Service.RespuestaConsultaServicePort
import com.example.shared.security.authorizeRole
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class RespuestaConsultaController(
    private val respuestaService: RespuestaConsultaServicePort
) {

    fun Route.respuestaConsultaRoutes() {
        route("/api/consultas/{consultaId}/respuestas") {

            // Rutas públicas - Ver respuestas
            get {
                val consultaId = call.parameters["consultaId"]?.toIntOrNull()

                if (consultaId == null) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to "ID de consulta inválido")
                    )
                    return@get
                }

                val respuestas = respuestaService.getRespuestasByConsultaId(consultaId)
                call.respond(HttpStatusCode.OK, respuestas)
            }

            // Rutas protegidas
            authenticate("auth-jwt") {

                // Solo abogados (rol 2) pueden responder consultas
                authorizeRole(2) {
                    post {
                        val consultaId = call.parameters["consultaId"]?.toIntOrNull()
                        val principal = call.principal<JWTPrincipal>()
                        val abogadoId = principal?.payload?.getClaim("userId")?.asString()

                        if (consultaId == null) {
                            call.respond(
                                HttpStatusCode.BadRequest,
                                mapOf("error" to "ID de consulta inválido")
                            )
                            return@post
                        }

                        if (abogadoId == null) {
                            call.respond(
                                HttpStatusCode.Unauthorized,
                                mapOf("error" to "Abogado no identificado")
                            )
                            return@post
                        }

                        val request = call.receive<CreateRespuestaRequest>()
                        val respuesta = respuestaService.createRespuesta(consultaId, abogadoId, request)

                        if (respuesta != null) {
                            call.respond(HttpStatusCode.Created, respuesta)
                        } else {
                            call.respond(
                                HttpStatusCode.BadRequest,
                                mapOf("error" to "No se pudo crear la respuesta")
                            )
                        }
                    }
                }
            }
        }

        // Rutas para respuestas individuales
        route("/api/respuestas") {

            get("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()

                if (id == null) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to "ID inválido")
                    )
                    return@get
                }

                val respuesta = respuestaService.getRespuestaById(id)

                if (respuesta != null) {
                    call.respond(HttpStatusCode.OK, respuesta)
                } else {
                    call.respond(
                        HttpStatusCode.NotFound,
                        mapOf("error" to "Respuesta no encontrada")
                    )
                }
            }

            authenticate("auth-jwt") {
                // Dar like a una respuesta (cualquier usuario autenticado)
                post("/{id}/like") {
                    val id = call.parameters["id"]?.toIntOrNull()

                    if (id == null) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            mapOf("error" to "ID inválido")
                        )
                        return@post
                    }

                    val liked = respuestaService.addLike(id)

                    if (liked) {
                        call.respond(
                            HttpStatusCode.OK,
                            mapOf("message" to "Like agregado")
                        )
                    } else {
                        call.respond(
                            HttpStatusCode.NotFound,
                            mapOf("error" to "Respuesta no encontrada")
                        )
                    }
                }

                // Eliminar respuesta (solo el abogado que la creó)
                delete("/{id}") {
                    val id = call.parameters["id"]?.toIntOrNull()
                    val principal = call.principal<JWTPrincipal>()
                    val abogadoId = principal?.payload?.getClaim("userId")?.asString()

                    if (id == null) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            mapOf("error" to "ID inválido")
                        )
                        return@delete
                    }

                    if (abogadoId == null) {
                        call.respond(
                            HttpStatusCode.Unauthorized,
                            mapOf("error" to "Usuario no identificado")
                        )
                        return@delete
                    }

                    // Verificar que la respuesta le pertenece
                    val respuesta = respuestaService.getRespuestaById(id)
                    if (respuesta == null) {
                        call.respond(
                            HttpStatusCode.NotFound,
                            mapOf("error" to "Respuesta no encontrada")
                        )
                        return@delete
                    }

                    if (respuesta.idAbogado != abogadoId) {
                        call.respond(
                            HttpStatusCode.Forbidden,
                            mapOf("error" to "No puedes eliminar esta respuesta")
                        )
                        return@delete
                    }

                    val deleted = respuestaService.deleteRespuesta(id)

                    if (deleted) {
                        call.respond(
                            HttpStatusCode.OK,
                            mapOf("message" to "Respuesta eliminada")
                        )
                    } else {
                        call.respond(
                            HttpStatusCode.NotFound,
                            mapOf("error" to "Respuesta no encontrada")
                        )
                    }
                }
            }
        }
    }
}