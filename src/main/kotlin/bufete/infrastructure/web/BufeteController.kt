package com.example.bufete.infrastructure.web

import com.example.bufete.domain.model.CreateBufeteRequest
import com.example.bufete.domain.model.Bufete
import com.example.bufete.domain.port.Service.BufeteServicePort
import com.example.shared.security.authorizeRole
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class BufeteController(
    private val bufeteService: BufeteServicePort
) {

    fun Route.bufeteRoutes() {
        route("/api/bufetes") {

            // Públicas
            get {
                val bufetes = bufeteService.getAllBufetes()
                call.respond(HttpStatusCode.OK, bufetes)
            }

            get("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()

                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID inválido"))
                    return@get
                }

                val bufete = bufeteService.getBufeteById(id)

                if (bufete != null) {
                    call.respond(HttpStatusCode.OK, bufete)
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Bufete no encontrado"))
                }
            }

            // Buscar abogados de un bufete por especialidad
            get("/{bufeteId}/abogados/especialidad/{especialidadId}") {
                val bufeteId = call.parameters["bufeteId"]?.toIntOrNull()
                val especialidadId = call.parameters["especialidadId"]?.toIntOrNull()

                if (bufeteId == null || especialidadId == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "IDs inválidos"))
                    return@get
                }

                val abogados = bufeteService.getAbogadosByBufeteYEspecialidad(bufeteId, especialidadId)
                call.respond(HttpStatusCode.OK, abogados)
            }

            // Protegidas
            authenticate("auth-jwt") {

                // Solo abogados pueden crear bufetes
                authorizeRole(2) {
                    post {
                        val principal = call.principal<JWTPrincipal>()
                        val adminId = principal?.payload?.getClaim("userId")?.asString()

                        if (adminId == null) {
                            call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Usuario no identificado"))
                            return@post
                        }

                        val request = call.receive<CreateBufeteRequest>()
                        val bufete = bufeteService.createBufete(adminId, request)

                        if (bufete != null) {
                            call.respond(HttpStatusCode.Created, bufete)
                        } else {
                            call.respond(HttpStatusCode.BadRequest, mapOf("error" to "No se pudo crear el bufete"))
                        }
                    }
                }

                get("/mis-bufetes") {
                    val principal = call.principal<JWTPrincipal>()
                    val adminId = principal?.payload?.getClaim("userId")?.asString()

                    if (adminId == null) {
                        call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Usuario no identificado"))
                        return@get
                    }

                    val bufetes = bufeteService.getBufetesByAdminId(adminId)
                    call.respond(HttpStatusCode.OK, bufetes)
                }

                put("/{id}") {
                    val id = call.parameters["id"]?.toIntOrNull()
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.getClaim("userId")?.asString()

                    if (id == null) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID inválido"))
                        return@put
                    }

                    if (userId == null) {
                        call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Usuario no identificado"))
                        return@put
                    }

                    // Verificar que el bufete le pertenece
                    val bufete = bufeteService.getBufeteById(id)
                    if (bufete == null) {
                        call.respond(HttpStatusCode.NotFound, mapOf("error" to "Bufete no encontrado"))
                        return@put
                    }

                    if (bufete.adminBufeteId != userId) {
                        call.respond(HttpStatusCode.Forbidden, mapOf("error" to "No puedes modificar este bufete"))
                        return@put
                    }

                    val updatedData = call.receive<Bufete>()
                    val updated = bufeteService.updateBufete(id, updatedData)

                    if (updated != null) {
                        call.respond(HttpStatusCode.OK, updated)
                    } else {
                        call.respond(HttpStatusCode.NotFound, mapOf("error" to "Bufete no encontrado"))
                    }
                }

                delete("/{id}") {
                    val id = call.parameters["id"]?.toIntOrNull()
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.getClaim("userId")?.asString()

                    if (id == null) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID inválido"))
                        return@delete
                    }

                    if (userId == null) {
                        call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Usuario no identificado"))
                        return@delete
                    }

                    // Verificar que el bufete le pertenece
                    val bufete = bufeteService.getBufeteById(id)
                    if (bufete == null) {
                        call.respond(HttpStatusCode.NotFound, mapOf("error" to "Bufete no encontrado"))
                        return@delete
                    }

                    if (bufete.adminBufeteId != userId) {
                        call.respond(HttpStatusCode.Forbidden, mapOf("error" to "No puedes eliminar este bufete"))
                        return@delete
                    }

                    val deleted = bufeteService.deleteBufete(id)

                    if (deleted) {
                        call.respond(HttpStatusCode.OK, mapOf("message" to "Bufete eliminado"))
                    } else {
                        call.respond(HttpStatusCode.NotFound, mapOf("error" to "Bufete no encontrado"))
                    }
                }
            }
        }
    }
}