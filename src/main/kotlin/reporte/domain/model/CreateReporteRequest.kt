package com.example.reporte.domain.model
import kotlinx.serialization.Serializable
@Serializable
data class CreateReporteRequest(
    val usuarioReportadoId: String,
    val motivoReporteId: Int,
    val consultaId: Int?,
    val comentarios: String?
)