package com.example.municipio.infrastructure.web

import com.example.municipio.domain.port.MunicipioServicePort
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class MunicipioController(
    private val municipioService: MunicipioServicePort
) {

    fun Route.municipioRoutes() {
        route("/api/municipios") {

            get {
                val estadoId = call.request.queryParameters["estadoId"]?.toIntOrNull()

                val municipios = if (estadoId != null) {
                    municipioService.getMunicipiosByEstadoId(estadoId)
                } else {
                    municipioService.getAllMunicipios()
                }

                call.respond(HttpStatusCode.OK, municipios)
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

                val municipio = municipioService.getMunicipioById(id)

                if (municipio != null) {
                    call.respond(HttpStatusCode.OK, municipio)
                } else {
                    call.respond(
                        HttpStatusCode.NotFound,
                        mapOf("error" to "Municipio no encontrado")
                    )
                }
            }
        }
    }
}