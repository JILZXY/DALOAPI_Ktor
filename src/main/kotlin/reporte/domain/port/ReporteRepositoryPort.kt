package com.example.reporte.domain.port

import com.example.reporte.domain.model.Reporte

interface ReporteRepositoryPort {
    suspend fun findAll(): List<Reporte>
    suspend fun findById(id: Int): Reporte?
    suspend fun findByUsuarioReportadoId(usuarioId: String): List<Reporte>
    suspend fun findByUsuarioReportaId(usuarioId: String): List<Reporte>
    suspend fun create(reporte: Reporte): Reporte?
    suspend fun delete(id: Int): Boolean
}