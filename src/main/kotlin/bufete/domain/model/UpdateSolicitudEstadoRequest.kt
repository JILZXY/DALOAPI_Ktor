package com.example.bufete.domain.model

@Serializable
data class UpdateSolicitudEstadoRequest(
    val estado: String // Aprobado, Rechazado
)