package com.example.bufete.domain.model
import kotlinx.serialization.Serializable
@Serializable
data class CreateCalificacionBufeteRequest(
    val calificacionGeneral: Double,
    val mensaje: String
)