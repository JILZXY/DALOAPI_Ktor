package com.example.consulta.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class UpdateEstadoConsultaRequest(
    val estado: String // abierta, atendida, cerrada
)