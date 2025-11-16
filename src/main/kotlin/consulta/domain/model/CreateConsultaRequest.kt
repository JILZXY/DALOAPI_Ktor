package com.example.consulta.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateConsultaRequest(
    val titulo: String,
    val pregunta: String,
    val esPrivada: Boolean = false,
    val especialidadesIds: List<Int>
)