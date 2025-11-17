package com.example.reporte.domain.port

import com.example.reporte.domain.model.MotivoReporte

interface MotivoReporteRepositoryPort {
    suspend fun findAll(): List<MotivoReporte>
    suspend fun findById(id: Int): MotivoReporte?
}