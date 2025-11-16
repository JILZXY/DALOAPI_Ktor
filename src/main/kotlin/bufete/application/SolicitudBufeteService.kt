package com.example.bufete.application

import com.example.bufete.domain.model.SolicitudBufete
import com.example.bufete.domain.port.SolicitudBufeteServicePort
import com.example.bufete.domain.port.SolicitudBufeteRepositoryPort
import com.example.bufete.domain.port.BufeteRepositoryPort
import com.example.calificacion.domain.port.CalificacionRepositoryPort

class SolicitudBufeteService(
    private val solicitudRepository: SolicitudBufeteRepositoryPort,
    private val bufeteRepository: BufeteRepositoryPort,
    private val calificacionRepository: CalificacionRepositoryPort
) : SolicitudBufeteServicePort {

    override suspend fun getAllSolicitudes(): List<SolicitudBufete> {
        return solicitudRepository.findAll()
    }

    override suspend fun getSolicitudById(id: Int): SolicitudBufete? {
        return solicitudRepository.findById(id)
    }

    override suspend fun getSolicitudesByBufeteId(bufeteId: Int): List<SolicitudBufete> {
        return solicitudRepository.findByBufeteId(bufeteId)
    }

    override suspend fun getSolicitudesByAbogadoId(abogadoId: String): List<SolicitudBufete> {
        return solicitudRepository.findByAbogadoId(abogadoId)
    }

    override suspend fun createSolicitud(abogadoId: String, bufeteId: Int): SolicitudBufete? {
        val solicitud = SolicitudBufete(
            abogadoId = abogadoId,
            bufeteId = bufeteId,
            estado = "Pendiente"
        )

        return solicitudRepository.create(solicitud)
    }

    override suspend fun aprobarSolicitud(id: Int): Boolean {
        val aprobado = solicitudRepository.updateEstado(id, "Aprobado")

        if (aprobado) {
            // Obtener la solicitud para recalcular calificación del bufete
            val solicitud = solicitudRepository.findById(id)
            if (solicitud != null) {
                // Recalcular calificación promedio del bufete
                bufeteRepository.calculateCalificacionPromedio(solicitud.bufeteId)
            }
        }

        return aprobado
    }

    override suspend fun rechazarSolicitud(id: Int): Boolean {
        return solicitudRepository.updateEstado(id, "Rechazado")
    }
}