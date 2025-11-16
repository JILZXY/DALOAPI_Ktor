package com.example.calificacion.domain.model
import kotlinx.serialization.Serializable
@Serializable
data class CreateCalificacionRequest(
    val atencion: Int, // 1-5
    val profesionalismo: Int, // 1-5
    val claridad: Int, // 1-5
    val empatia: Int, // 1-5
    val comentarioOpcional: String?
)