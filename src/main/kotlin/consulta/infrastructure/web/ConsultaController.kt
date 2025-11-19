package com.example.consulta.infrastructure.web

import com.example.consulta.domain.model.CreateConsultaRequest
import com.example.consulta.domain.model.UpdateEstadoConsultaRequest
import com.example.consulta.domain.port.Service.ConsultaServicePort
import com.example.shared.security.authorizeRole
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class ConsultaController(
    private val consultaService: ConsultaServicePort
) {

    fun Route.consultaRoutes() {
        route("/api/consultas") {

            // Rutas públicas - Ver consultas públicas
            get {
                val especialidadId = call.request.queryParameters["especialidadId"]?.toIntOrNull()

                val consultas = if (especialidadId != null) {
                    consultaService.getConsultasByEspecialidad(especialidadId)
                } else {
                    consultaService.getAllConsultas(includePrivate = false)
                }

                call.respond(HttpStatusCode.OK, consultas)
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

                val consulta = consultaService.getConsultaById(id)

                if (consulta != null) {
                    call.respond(HttpStatusCode.OK, consulta)
                } else {
                    call.respond(
                        HttpStatusCode.NotFound,
                        mapOf("error" to "Consulta no encontrada")
                    )
                }
            }

            // Filtrar consultas públicas por materia
            get("/materia") {
                val materiaId = call.request.queryParameters["materiaId"]?.toIntOrNull()

                if (materiaId == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "materiaId requerido"))
                    return@get
                }

                val consultas = consultaService.getConsultasByMateria(materiaId)
                call.respond(HttpStatusCode.OK, consultas)
            }

            // Filtrar consultas públicas por localidad del autor
            get("/localidad") {
                val estadoId = call.request.queryParameters["estadoId"]?.toIntOrNull()
                val municipioId = call.request.queryParameters["municipioId"]?.toIntOrNull()

                if (estadoId == null && municipioId == null) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to "Debe proporcionar estadoId o municipioId")
                    )
                    return@get
                }

                val consultas = consultaService.getConsultasByLocalidad(estadoId, municipioId)
                call.respond(HttpStatusCode.OK, consultas)
            }

            // Filtrar consultas públicas por materia Y localidad
            get("/materiaLocalidad") {
                val materiaId = call.request.queryParameters["materiaId"]?.toIntOrNull()
                val estadoId = call.request.queryParameters["estadoId"]?.toIntOrNull()
                val municipioId = call.request.queryParameters["municipioId"]?.toIntOrNull()

                if (materiaId == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "materiaId requerido"))
                    return@get
                }

                if (estadoId == null && municipioId == null) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to "Debe proporcionar estadoId o municipioId")
                    )
                    return@get
                }

                val consultas = consultaService.getConsultasByMateriaYLocalidad(
                    materiaId, estadoId, municipioId
                )
                call.respond(HttpStatusCode.OK, consultas)
            }

            // Contar total de consultas de un usuario
            get("/usuario/{id}/total") {
                val id = call.parameters["id"]

                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID inválido"))
                    return@get
                }

                val total = consultaService.getTotalConsultasByUsuarioId(id)
                call.respond(HttpStatusCode.OK, mapOf("total" to total))
            }

            // Obtener todas las consultas de un usuario (ya existe como /mis-consultas pero solo para el autenticado)
            // Este endpoint permite ver consultas de cualquier usuario por ID
            get("/por-id/{id}") {
                val id = call.parameters["id"]

                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID inválido"))
                    return@get
                }

                val consultas = consultaService.getConsultasByUsuarioId(id)
                call.respond(HttpStatusCode.OK, consultas)
            }

            // Rutas protegidas - Autenticado
            authenticate("auth-jwt") {

                // Solo clientes (rol 1) pueden crear consultas
                authorizeRole(1) {
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

                        val request = call.receive<CreateConsultaRequest>()
                        val consulta = consultaService.createConsulta(userId, request)

                        if (consulta != null) {
                            call.respond(HttpStatusCode.Created, consulta)
                        } else {
                            call.respond(
                                HttpStatusCode.BadRequest,
                                mapOf("error" to "No se pudo crear la consulta")
                            )
                        }
                    }
                }

                // Ver mis consultas (cualquier usuario autenticado)
                get("/mis-consultas") {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.getClaim("userId")?.asString()

                    if (userId == null) {
                        call.respond(
                            HttpStatusCode.Unauthorized,
                            mapOf("error" to "Usuario no identificado")
                        )
                        return@get
                    }

                    val consultas = consultaService.getConsultasByUsuarioId(userId)
                    call.respond(HttpStatusCode.OK, consultas)
                }

                // Actualizar estado de consulta (solo el dueño)
                patch("/{id}/estado") {
                    val id = call.parameters["id"]?.toIntOrNull()
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.getClaim("userId")?.asString()

                    if (id == null) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            mapOf("error" to "ID inválido")
                        )
                        return@patch
                    }

                    if (userId == null) {
                        call.respond(
                            HttpStatusCode.Unauthorized,
                            mapOf("error" to "Usuario no identificado")
                        )
                        return@patch
                    }

                    // Verificar que la consulta le pertenece
                    val consulta = consultaService.getConsultaById(id)
                    if (consulta == null) {
                        call.respond(
                            HttpStatusCode.NotFound,
                            mapOf("error" to "Consulta no encontrada")
                        )
                        return@patch
                    }

                    if (consulta.idUsuario != userId) {
                        call.respond(
                            HttpStatusCode.Forbidden,
                            mapOf("error" to "No puedes modificar esta consulta")
                        )
                        return@patch
                    }

                    val request = call.receive<UpdateEstadoConsultaRequest>()
                    val updated = consultaService.updateEstadoConsulta(id, request.estado)

                    if (updated) {
                        call.respond(
                            HttpStatusCode.OK,
                            mapOf("message" to "Estado actualizado")
                        )
                    } else {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            mapOf("error" to "Estado inválido")
                        )
                    }
                }

                // Eliminar consulta (solo el dueño)
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

                    // Verificar que la consulta le pertenece
                    val consulta = consultaService.getConsultaById(id)
                    if (consulta == null) {
                        call.respond(
                            HttpStatusCode.NotFound,
                            mapOf("error" to "Consulta no encontrada")
                        )
                        return@delete
                    }

                    if (consulta.idUsuario != userId) {
                        call.respond(
                            HttpStatusCode.Forbidden,
                            mapOf("error" to "No puedes eliminar esta consulta")
                        )
                        return@delete
                    }

                    val deleted = consultaService.deleteConsulta(id)

                    if (deleted) {
                        call.respond(
                            HttpStatusCode.OK,
                            mapOf("message" to "Consulta eliminada")
                        )
                    } else {
                        call.respond(
                            HttpStatusCode.NotFound,
                            mapOf("error" to "Consulta no encontrada")
                        )
                    }
                }
            }
        }
    }
}