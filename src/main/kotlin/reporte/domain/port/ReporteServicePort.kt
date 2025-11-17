package com.example.reporte.domain.port

import com.example.reporte.domain.model.CreateReporteRequest
import com.example.reporte.domain.model.MotivoReporte
import com.example.reporte.domain.model.Reporte

interface ReporteServicePort {
    suspend fun getAllReportes(): List<Reporte>
    suspend fun getReporteById(id: Int): Reporte?
    suspend fun getReportesByUsuarioReportadoId(usuarioId: String): List<Reporte>
    suspend fun getReportesByUsuarioReportaId(usuarioId: String): List<Reporte>
    suspend fun createReporte(usuarioReportaId: String, request: CreateReporteRequest): Reporte?
    suspend fun deleteReporte(id: Int): Boolean
    suspend fun getAllMotivosReporte(): List<MotivoReporte>
}