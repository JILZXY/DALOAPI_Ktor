package com.example.reporte.domain.model
import kotlinx.serialization.Serializable
@Serializable
data class MotivoReporte(
    val id: Int,
    val nombre: String,
    val descripcion: String?
)