package com.example.reporte.domain

import com.example.reporte.domain.model.CreateReporteRequest
import com.example.reporte.domain.model.Reporte
import com.example.reporte.domain.port.MotivoReporteRepositoryPort
import com.example.reporte.domain.port.ReporteRepositoryPort
import com.example.reporte.domain.port.ReporteServicePort
import com.example.reporte.domain.model.MotivoReporte


class ReporteService(
    private val reporteRepository: ReporteRepositoryPort,
    private val motivoReporteRepository: MotivoReporteRepositoryPort
) : ReporteServicePort {

    override suspend fun getAllReportes(): List<Reporte> {
        return reporteRepository.findAll()
    }

    override suspend fun getReporteById(id: Int): Reporte? {
        return reporteRepository.findById(id)
    }

    override suspend fun getReportesByUsuarioReportadoId(usuarioId: String): List<Reporte> {
        return reporteRepository.findByUsuarioReportadoId(usuarioId)
    }

    override suspend fun getReportesByUsuarioReportaId(usuarioId: String): List<Reporte> {
        return reporteRepository.findByUsuarioReportaId(usuarioId)
    }

    override suspend fun createReporte(usuarioReportaId: String, request: CreateReporteRequest): Reporte? {
        // No permitir auto-reporte
        if (usuarioReportaId == request.usuarioReportadoId) {
            return null
        }

        val reporte = Reporte(
            usuarioReportaId = usuarioReportaId,
            usuarioReportadoId = request.usuarioReportadoId,
            motivoReporteId = request.motivoReporteId,
            consultaId = request.consultaId,
            comentarios = request.comentarios
        )

        return reporteRepository.create(reporte)
    }

    override suspend fun deleteReporte(id: Int): Boolean {
        return reporteRepository.delete(id)
    }

    override suspend fun getAllMotivosReporte(): List<MotivoReporte> {
        return motivoReporteRepository.findAll()
    }
}