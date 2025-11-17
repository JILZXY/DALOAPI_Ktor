package com.example.chat.infrastructure.web

import com.example.chat.domain.model.SendMensajeRequest
import com.example.chat.domain.port.ChatServicePort
import com.example.chat.domain.port.MensajeServicePort
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class MensajeController(
    private val mensajeService: MensajeServicePort,
    private val chatService: ChatServicePort
) {

    fun Route.mensajeRoutes() {
        route("/api/chats/{chatId}/mensajes") {

            authenticate("auth-jwt") {

                // Obtener todos los mensajes de un chat
                get {
                    val chatId = call.parameters["chatId"]?.toIntOrNull()
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.getClaim("userId")?.asString()

                    if (chatId == null) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            mapOf("error" to "ID de chat inválido")
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

                    // Verificar que el usuario sea parte del chat
                    val chat = chatService.getChatById(chatId)
                    if (chat == null) {
                        call.respond(
                            HttpStatusCode.NotFound,
                            mapOf("error" to "Chat no encontrado")
                        )
                        return@get
                    }

                    if (chat.usuarioClienteId != userId && chat.usuarioAbogadoId != userId) {
                        call.respond(
                            HttpStatusCode.Forbidden,
                            mapOf("error" to "No tienes acceso a este chat")
                        )
                        return@get
                    }

                    val mensajes = mensajeService.getMensajesByChatId(chatId)
                    call.respond(HttpStatusCode.OK, mensajes)
                }

                // Enviar un mensaje
                post {
                    val chatId = call.parameters["chatId"]?.toIntOrNull()
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.getClaim("userId")?.asString()

                    if (chatId == null) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            mapOf("error" to "ID de chat inválido")
                        )
                        return@post
                    }

                    if (userId == null) {
                        call.respond(
                            HttpStatusCode.Unauthorized,
                            mapOf("error" to "Usuario no identificado")
                        )
                        return@post
                    }

                    // Verificar que el usuario sea parte del chat
                    val chat = chatService.getChatById(chatId)
                    if (chat == null) {
                        call.respond(
                            HttpStatusCode.NotFound,
                            mapOf("error" to "Chat no encontrado")
                        )
                        return@post
                    }

                    if (chat.usuarioClienteId != userId && chat.usuarioAbogadoId != userId) {
                        call.respond(
                            HttpStatusCode.Forbidden,
                            mapOf("error" to "No tienes acceso a este chat")
                        )
                        return@post
                    }

                    val request = call.receive<SendMensajeRequest>()
                    val mensaje = mensajeService.sendMensaje(chatId, userId, request.mensaje)

                    if (mensaje != null) {
                        call.respond(HttpStatusCode.Created, mensaje)
                    } else {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            mapOf("error" to "No se pudo enviar el mensaje")
                        )
                    }
                }
            }
        }

        route("/api/mensajes") {
            authenticate("auth-jwt") {

                // Eliminar un mensaje (solo el remitente)
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

                    // Verificar que el mensaje le pertenece
                    val mensaje = mensajeService.getMensajeById(id)
                    if (mensaje == null) {
                        call.respond(
                            HttpStatusCode.NotFound,
                            mapOf("error" to "Mensaje no encontrado")
                        )
                        return@delete
                    }

                    if (mensaje.remitenteId != userId) {
                        call.respond(
                            HttpStatusCode.Forbidden,
                            mapOf("error" to "No puedes eliminar este mensaje")
                        )
                        return@delete
                    }

                    val deleted = mensajeService.deleteMensaje(id)

                    if (deleted) {
                        call.respond(
                            HttpStatusCode.OK,
                            mapOf("message" to "Mensaje eliminado")
                        )
                    } else {
                        call.respond(
                            HttpStatusCode.NotFound,
                            mapOf("error" to "Mensaje no encontrado")
                        )
                    }
                }
            }
        }
    }
}