package com.example.chat.domain.port.Repository

import com.example.chat.domain.model.Chat

interface ChatRepositoryPort {
    suspend fun findAll(): List<Chat>
    suspend fun findById(id: Int): Chat?
    suspend fun findByUsuarioId(usuarioId: String): List<Chat>
    suspend fun findByParticipantes(clienteId: String, abogadoId: String): Chat?
    suspend fun create(chat: Chat): Chat?
    suspend fun delete(id: Int): Boolean
}