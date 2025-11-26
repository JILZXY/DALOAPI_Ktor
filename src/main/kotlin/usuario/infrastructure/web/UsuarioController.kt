package com.example.usuario.infrastructure.web

import com.example.shared.security.authorizeRole
import com.example.usuario.domain.model.LoginRequest
import com.example.usuario.domain.model.RegisterAbogadoRequest
import com.example.usuario.domain.model.RegisterRequest
import com.example.usuario.domain.model.Usuario
import com.example.usuario.domain.port.Service.UsuarioServicePort
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class UsuarioController(
    private val usuarioService: UsuarioServicePort
) {

    fun Route.usuarioRoutes() {
        route("/api/auth") {
            authenticate("auth-jwt") {
                authorizeRole(3) {
                    get("/inactivos") {
                        val usuariosInactivos = usuarioService.getUsuariosInactivos()
                        call.respond(HttpStatusCode.OK, usuariosInactivos)
                    }

                    // Activar un usuario (principalmente para aprobar abogados)
                    patch("/{id}/activar") {
                        val id = call.parameters["id"]

                        if (id == null) {
                            call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID inválido"))
                            return@patch
                        }

                        val activado = usuarioService.activarUsuario(id)

                        if (activado) {
                            call.respond(HttpStatusCode.OK, mapOf("message" to "Usuario activado"))
                        } else {
                            call.respond(HttpStatusCode.NotFound, mapOf("error" to "Usuario no encontrado"))
                        }
                    }
                }

                // Buscar por email (cualquier usuario autenticado)
                get("/email/{email}") {
                    val email = call.parameters["email"]

                    if (email == null) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Email inválido"))
                        return@get
                    }

                    val usuario = usuarioService.getUsuarioByEmail(email)

                    if (usuario != null) {
                        call.respond(HttpStatusCode.OK, usuario)
                    } else {
                        call.respond(HttpStatusCode.NotFound, mapOf("error" to "Usuario no encontrado"))
                    }
                }
            }
            post("/register") {
                val request = call.receive<RegisterRequest>()

                val response = usuarioService.register(request)

                if (response != null) {
                    call.respond(HttpStatusCode.Created, response)
                } else {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to "No se pudo registrar el usuario. El email puede estar en uso.")
                    )
                }
            }

            post("/register/abogado") {
                val request = call.receive<RegisterAbogadoRequest>()
                val response = usuarioService.registerAbogado(request) // Nuevo método en el servicio

                if (response != null) {
                    call.respond(HttpStatusCode.Created, response)
                } else {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to "No se pudo registrar el abogado. El email puede estar en uso.")
                    )
                }
            }

            post("/login") {
                val request = call.receive<LoginRequest>()

                val response = usuarioService.login(request)

                if (response != null) {
                    call.respond(HttpStatusCode.OK, response)
                } else {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        mapOf("error" to "Credenciales inválidas")
                    )
                }
            }
        }

        route("/api/usuarios") {
            authenticate("auth-jwt") {
                get {
                    val usuarios = usuarioService.getAllUsuarios()
                    call.respond(HttpStatusCode.OK, usuarios)
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

                    val usuario = usuarioService.getUsuarioById(id)

                    if (usuario != null) {
                        call.respond(HttpStatusCode.OK, usuario)
                    } else {
                        call.respond(
                            HttpStatusCode.NotFound,
                            mapOf("error" to "Usuario no encontrado")
                        )
                    }
                }

                get("/me") {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.getClaim("userId")?.asString()

                    if (userId != null) {
                        val usuario = usuarioService.getUsuarioById(userId)
                        if (usuario != null) {
                            call.respond(HttpStatusCode.OK, usuario)
                        } else {
                            call.respond(
                                HttpStatusCode.NotFound,
                                mapOf("error" to "Usuario no encontrado")
                            )
                        }
                    } else {
                        call.respond(
                            HttpStatusCode.Unauthorized,
                            mapOf("error" to "Token inválido")
                        )
                    }
                }

                put("/{id}") {
                    val id = call.parameters["id"]

                    if (id == null) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            mapOf("error" to "ID inválido")
                        )
                        return@put
                    }

                    val usuario = call.receive<Usuario>()
                    val updated = usuarioService.updateUsuario(id, usuario)

                    if (updated != null) {
                        call.respond(HttpStatusCode.OK, updated)
                    } else {
                        call.respond(
                            HttpStatusCode.NotFound,
                            mapOf("error" to "Usuario no encontrado")
                        )
                    }
                }

                delete("/{id}") {
                    val id = call.parameters["id"]

                    if (id == null) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            mapOf("error" to "ID inválido")
                        )
                        return@delete
                    }

                    val deleted = usuarioService.deleteUsuario(id)

                    if (deleted) {
                        call.respond(
                            HttpStatusCode.OK,
                            mapOf("message" to "Usuario eliminado")
                        )
                    } else {
                        call.respond(
                            HttpStatusCode.NotFound,
                            mapOf("error" to "Usuario no encontrado")
                        )
                    }
                }
            }
        }
    }
}