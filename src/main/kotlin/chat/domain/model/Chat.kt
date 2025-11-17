package com.example.chat.domain.model

import com.example.usuario.domain.model.Usuario
import com.example.usuario.domain.model.Abogado
import kotlinx.serialization.Serializable

@Serializable
data class Chat(
    val id: Int = 0,
    val usuarioClienteId: String,
    val usuarioAbogadoId: String,
    val fechaInicio: String = "",
    val cliente: Usuario? = null,
    val abogado: Abogado? = null,
    val ultimoMensaje: Mensaje? = null
)