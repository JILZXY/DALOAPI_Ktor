package com.example.reporte.domain.model

import com.example.usuario.domain.model.Usuario
import kotlinx.serialization.Serializable

@Serializable
data class Reporte(
    val id: Int = 0,
    val usuarioReportaId: String,
    val usuarioReportadoId: String,
    val motivoReporteId: Int,
    val consultaId: Int?,
    val fechaReporte: String = "",
    val comentarios: String?,
    val usuarioReporta: Usuario? = null,
    val usuarioReportado: Usuario? = null,
    val motivoReporte: MotivoReporte? = null
)