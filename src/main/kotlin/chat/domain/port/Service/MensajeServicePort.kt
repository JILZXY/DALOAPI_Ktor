package com.example.chat.domain.port.Service

import com.example.chat.domain.model.Mensaje

interface MensajeServicePort {
    suspend fun getMensajesByChatId(chatId: Int): List<Mensaje>
    suspend fun getMensajeById(id: Int): Mensaje?
    suspend fun sendMensaje(chatId: Int, remitenteId: String, mensaje: String): Mensaje?
    suspend fun deleteMensaje(id: Int): Boolean
}