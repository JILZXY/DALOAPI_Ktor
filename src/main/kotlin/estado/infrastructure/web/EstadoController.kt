package com.example.estado.infrastructure.web

import com.example.estado.domain.port.EstadoServicePort
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.Route
import io.ktor.server.response.*
import io.ktor.server.routing.*

class EstadoController(
    private val estadoService: EstadoServicePort
) {

    fun Route.estadoRoutes() {
        route("/api/estados") {

            get {
                val estados = estadoService.getAllEstados()
                call.respond(HttpStatusCode.OK, estados)
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

                val estado = estadoService.getEstadoById(id)

                if (estado != null) {
                    call.respond(HttpStatusCode.OK, estado)
                } else {
                    call.respond(
                        HttpStatusCode.NotFound,
                        mapOf("error" to "Estado no encontrado")
                    )
                }
            }
        }
    }
}