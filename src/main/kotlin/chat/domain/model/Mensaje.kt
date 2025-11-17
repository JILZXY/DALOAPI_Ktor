package com.example.chat.domain.model

import com.example.usuario.domain.model.Usuario
import kotlinx.serialization.Serializable

@Serializable
data class Mensaje(
    val id: Int = 0,
    val chatId: Int,
    val remitenteId: String,
    val mensaje: String,
    val fecha: String = "",
    val remitente: Usuario? = null
)