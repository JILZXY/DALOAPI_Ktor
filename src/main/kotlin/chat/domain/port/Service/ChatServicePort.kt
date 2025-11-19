package com.example.chat.domain.port.Service

import com.example.chat.domain.model.Chat

interface ChatServicePort {
    suspend fun getAllChats(): List<Chat>
    suspend fun getChatById(id: Int): Chat?
    suspend fun getChatsByUsuarioId(usuarioId: String): List<Chat>
    suspend fun createChat(clienteId: String, abogadoId: String): Chat?
    suspend fun deleteChat(id: Int): Boolean
    suspend fun buscarChatsPorNombreParticipante(usuarioId: String, nombre: String): List<Chat>
}