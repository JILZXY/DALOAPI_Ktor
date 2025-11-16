package com.example.consulta.domain.model

import com.example.usuario.domain.model.Abogado
import kotlinx.serialization.Serializable

@Serializable
data class RespuestaConsulta(
    val idRespuesta: Int = 0,
    val idConsulta: Int,
    val idAbogado: String,
    val respuesta: String,
    val fechaRespuesta: String = "",
    val likes: Int = 0,
    val abogado: Abogado? = null
)