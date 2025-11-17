package com.example.chat.application

import com.example.chat.domain.model.Mensaje
import com.example.chat.domain.port.MensajeRepositoryPort
import com.example.chat.domain.port.MensajeServicePort

class MensajeService(
    private val mensajeRepository: MensajeRepositoryPort
) : MensajeServicePort {

    override suspend fun getMensajesByChatId(chatId: Int): List<Mensaje> {
        return mensajeRepository.findByChatId(chatId)
    }

    override suspend fun getMensajeById(id: Int): Mensaje? {
        return mensajeRepository.findById(id)
    }

    override suspend fun sendMensaje(chatId: Int, remitenteId: String, mensaje: String): Mensaje? {
        val nuevoMensaje = Mensaje(
            chatId = chatId,
            remitenteId = remitenteId,
            mensaje = mensaje
        )

        return mensajeRepository.create(nuevoMensaje)
    }

    override suspend fun deleteMensaje(id: Int): Boolean {
        return mensajeRepository.delete(id)
    }
}