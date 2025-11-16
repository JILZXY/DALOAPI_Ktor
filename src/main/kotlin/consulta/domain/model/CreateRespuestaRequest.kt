package com.example.consulta.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateRespuestaRequest(
    val respuesta: String
)