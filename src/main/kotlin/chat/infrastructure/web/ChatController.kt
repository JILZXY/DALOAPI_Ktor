package com.example.chat.infrastructure.web

import com.example.chat.domain.model.CreateChatRequest
import com.example.chat.domain.port.Service.ChatServicePort
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class ChatController(
    private val chatService: ChatServicePort
) {

    fun Route.chatRoutes() {
        route("/api/chats") {

            authenticate("auth-jwt") {

                // Obtener todos los chats del usuario autenticado
                get {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.getClaim("userId")?.asString()

                    if (userId == null) {
                        call.respond(
                            HttpStatusCode.Unauthorized,
                            mapOf("error" to "Usuario no identificado")
                        )
                        return@get
                    }

                    val chats = chatService.getChatsByUsuarioId(userId)
                    call.respond(HttpStatusCode.OK, chats)
                }

                // Obtener un chat específico
                get("/{id}") {
                    val id = call.parameters["id"]?.toIntOrNull()
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.getClaim("userId")?.asString()

                    if (id == null) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            mapOf("error" to "ID inválido")
                        )
                        return@get
                    }

                    if (userId == null) {
                        call.respond(
                            HttpStatusCode.Unauthorized,
                            mapOf("error" to "Usuario no identificado")
                        )
                        return@get
                    }

                    val chat = chatService.getChatById(id)

                    if (chat == null) {
                        call.respond(
                            HttpStatusCode.NotFound,
                            mapOf("error" to "Chat no encontrado")
                        )
                        return@get
                    }

                    // Verificar que el usuario sea parte del chat
                    if (chat.usuarioClienteId != userId && chat.usuarioAbogadoId != userId) {
                        call.respond(
                            HttpStatusCode.Forbidden,
                            mapOf("error" to "No tienes acceso a este chat")
                        )
                        return@get
                    }

                    call.respond(HttpStatusCode.OK, chat)
                }

                // Crear un nuevo chat (cliente inicia chat con abogado)
                post {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.getClaim("userId")?.asString()
                    val userRole = principal?.payload?.getClaim("rolId")?.asInt()

                    if (userId == null) {
                        call.respond(
                            HttpStatusCode.Unauthorized,
                            mapOf("error" to "Usuario no identificado")
                        )
                        return@post
                    }

                    // Solo clientes pueden iniciar chats
                    if (userRole != 1) {
                        call.respond(
                            HttpStatusCode.Forbidden,
                            mapOf("error" to "Solo los clientes pueden iniciar chats")
                        )
                        return@post
                    }

                    val request = call.receive<CreateChatRequest>()
                    val chat = chatService.createChat(userId, request.usuarioAbogadoId)

                    if (chat != null) {
                        call.respond(HttpStatusCode.Created, chat)
                    } else {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            mapOf("error" to "No se pudo crear el chat")
                        )
                    }
                }

                // Eliminar un chat
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

                    // Verificar que el usuario sea parte del chat
                    val chat = chatService.getChatById(id)
                    if (chat == null) {
                        call.respond(
                            HttpStatusCode.NotFound,
                            mapOf("error" to "Chat no encontrado")
                        )
                        return@delete
                    }

                    if (chat.usuarioClienteId != userId && chat.usuarioAbogadoId != userId) {
                        call.respond(
                            HttpStatusCode.Forbidden,
                            mapOf("error" to "No puedes eliminar este chat")
                        )
                        return@delete
                    }

                    val deleted = chatService.deleteChat(id)

                    if (deleted) {
                        call.respond(
                            HttpStatusCode.OK,
                            mapOf("message" to "Chat eliminado")
                        )
                    } else {
                        call.respond(
                            HttpStatusCode.NotFound,
                            mapOf("error" to "Chat no encontrado")
                        )
                    }
                }
            }
        }
    }
}