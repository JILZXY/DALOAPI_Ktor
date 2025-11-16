package com.example.bufete.domain.model

@Serializable
data class CreateCalificacionBufeteRequest(
    val calificacionGeneral: Double,
    val mensaje: String
)