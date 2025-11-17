package com.example.chat.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateChatRequest(
    val usuarioAbogadoId: String
)