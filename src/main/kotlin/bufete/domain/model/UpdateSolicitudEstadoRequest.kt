package com.example.bufete.domain.model
import kotlinx.serialization.Serializable
@Serializable
data class UpdateSolicitudEstadoRequest(
    val estado: String // Aprobado, Rechazado
)