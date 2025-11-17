package com.example.chat.domain.port

import com.example.chat.domain.model.Mensaje

interface MensajeRepositoryPort {
    suspend fun findByChatId(chatId: Int): List<Mensaje>
    suspend fun findById(id: Int): Mensaje?
    suspend fun create(mensaje: Mensaje): Mensaje?
    suspend fun delete(id: Int): Boolean
}