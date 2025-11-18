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

            get("/buscar/{nombre}") {
                val nombre = call.parameters["nombre"]

                if (nombre == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Nombre inválido"))
                    return@get
                }

                val abogados = abogadoService.buscarAbogadosPorNombre(nombre)
                call.respond(HttpStatusCode.OK, abogados)
            }

            // Filtrar abogados ACTIVOS por materia/especialidad
            get("/abogados/materia") {
                val materiaId = call.request.queryParameters["materiaId"]?.toIntOrNull()

                if (materiaId == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "materiaId requerido"))
                    return@get
                }

                val abogados = abogadoService.getAbogadosByEspecialidad(materiaId)
                call.respond(HttpStatusCode.OK, abogados)
            }

            // Filtrar abogados ACTIVOS por localidad (estado y/o municipio)
            get("/abogados/localidad") {
                val estadoId = call.request.queryParameters["estadoId"]?.toIntOrNull()
                val municipioId = call.request.queryParameters["municipioId"]?.toIntOrNull()

                if (estadoId == null && municipioId == null) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to "Debe proporcionar estadoId o municipioId")
                    )
                    return@get
                }

                val abogados = abogadoService.getAbogadosByLocalidad(estadoId, municipioId)
                call.respond(HttpStatusCode.OK, abogados)
            }

            // Filtro combinado: materia + localidad + ordenar por calificación
            get("/abogados/filtro") {
                val materiaId = call.request.queryParameters["materiaId"]?.toIntOrNull()
                val estadoId = call.request.queryParameters["estadoId"]?.toIntOrNull()
                val municipioId = call.request.queryParameters["municipioId"]?.toIntOrNull()
                val ordenarPorCalificacion = call.request.queryParameters["ordenarPorCalificacion"]?.toBoolean() ?: true

                val abogados = abogadoService.filtrarAbogados(
                    materiaId = materiaId,
                    estadoId = estadoId,
                    municipioId = municipioId,
                    ordenarPorCalificacion = ordenarPorCalificacion
                )

                call.respond(HttpStatusCode.OK, abogados)
            }

            // Obtener materias/especialidades de un abogado específico
            get("/materias/{id}") {
                val id = call.parameters["id"]

                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID inválido"))
                    return@get
                }

                val especialidades = abogadoService.getEspecialidadesByAbogadoId(id)
                call.respond(HttpStatusCode.OK, especialidades)
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