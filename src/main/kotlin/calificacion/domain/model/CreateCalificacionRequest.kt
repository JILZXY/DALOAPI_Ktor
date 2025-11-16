package com.example.calificacion.domain.model
import kotlinx.serialization.Serializable
@Serializable
data class CreateCalificacionRequest(
    val atencion: Int,
    val profesionalismo: Int,
    val claridad: Int,
    val empatia: Int,
    val comentarioOpcional: String?
)