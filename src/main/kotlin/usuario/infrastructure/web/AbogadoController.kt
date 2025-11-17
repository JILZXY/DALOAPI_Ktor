package com.example.usuario.infrastructure.web

import com.example.usuario.domain.model.Abogado
import com.example.usuario.domain.port.Service.AbogadoServicePort
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class AbogadoController(
    private val abogadoService: AbogadoServicePort
) {

    fun Route.abogadoRoutes() {
        route("/api/abogados") {
            // Rutas públicas (sin autenticación)
            get {
                val especialidadId = call.request.queryParameters["especialidadId"]?.toIntOrNull()

                val abogados = if (especialidadId != null) {
                    abogadoService.getAbogadosByEspecialidad(especialidadId)
                } else {
                    abogadoService.getAllAbogados()
                }

                call.respond(HttpStatusCode.OK, abogados)
            }

            get("/{id}") {
                val id = call.parameters["id"]

                if (id == null) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to "ID inválido")
                    )
                    return@get
                }

                val abogado = abogadoService.getAbogadoById(id)

                if (abogado != null) {
                    call.respond(HttpStatusCode.OK, abogado)
                } else {
                    call.respond(
                        HttpStatusCode.NotFound,
                        mapOf("error" to "Abogado no encontrado")
                    )
                }
            }

            // Rutas protegidas
            authenticate("auth-jwt") {
                put("/{id}") {
                    val id = call.parameters["id"]

                    if (id == null) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            mapOf("error" to "ID inválido")
                        )
                        return@put
                    }

                    val abogado = call.receive<Abogado>()
                    val updated = abogadoService.updateAbogado(id, abogado)

                    if (updated != null) {
                        call.respond(HttpStatusCode.OK, updated)
                    } else {
                        call.respond(
                            HttpStatusCode.NotFound,
                            mapOf("error" to "Abogado no encontrado")
                        )
                    }
                }
            }
        }
    }
}