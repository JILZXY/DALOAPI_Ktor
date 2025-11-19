package com.example.chat.application

import com.example.chat.domain.port.Repository.ChatRepositoryPort
import com.example.chat.domain.port.Service.ChatServicePort
import com.example.chat.domain.model.Chat

class ChatService(
    private val chatRepository: ChatRepositoryPort
) : ChatServicePort {

    override suspend fun getAllChats(): List<Chat> {
        return chatRepository.findAll()
    }

    override suspend fun getChatById(id: Int): Chat? {
        return chatRepository.findById(id)
    }

    override suspend fun getChatsByUsuarioId(usuarioId: String): List<Chat> {
        return chatRepository.findByUsuarioId(usuarioId)
    }

    override suspend fun createChat(clienteId: String, abogadoId: String): Chat? {
        // Verificar si ya existe un chat entre estos usuarios
        val existingChat = chatRepository.findByParticipantes(clienteId, abogadoId)
        if (existingChat != null) {
            return existingChat
        }

        val chat = Chat(
            usuarioClienteId = clienteId,
            usuarioAbogadoId = abogadoId
        )

        return chatRepository.create(chat)
    }

    override suspend fun buscarChatsPorNombreParticipante(usuarioId: String, nombre: String): List<Chat> {
        return chatRepository.findByUsuarioIdAndNombreParticipante(usuarioId, nombre)
    }

    override suspend fun deleteChat(id: Int): Boolean {
        return chatRepository.delete(id)
    }
}