package com.example.chat.domain.model
import kotlinx.serialization.Serializable
@Serializable
data class SendMensajeRequest(
    val mensaje: String
)