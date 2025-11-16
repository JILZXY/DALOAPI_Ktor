package com.example.bufete.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CalificacionBufete(
    val id: Int = 0,
    val usuarioClienteId: String,
    val bufeteId: Int,
    val calificacionGeneral: Double,
    val mensaje: String,
    val fechaCalificacion: String = ""
)