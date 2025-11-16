package com.example.bufete.application

import com.example.bufete.domain.model.CalificacionBufete
import com.example.bufete.domain.model.CreateCalificacionBufeteRequest
import com.example.bufete.domain.port.CalificacionBufeteServicePort
import com.example.bufete.domain.port.CalificacionBufeteRepositoryPort

class CalificacionBufeteService(
    private val calificacionBufeteRepository: CalificacionBufeteRepositoryPort
) : CalificacionBufeteServicePort {

    override suspend fun getCalificacionesByBufeteId(bufeteId: Int): List<CalificacionBufete> {
        return calificacionBufeteRepository.findByBufeteId(bufeteId)
    }

    override suspend fun createCalificacion(
        usuarioId: String,
        bufeteId: Int,
        request: CreateCalificacionBufeteRequest
    ): CalificacionBufete? {
        val calificacion = CalificacionBufete(
            usuarioClienteId = usuarioId,
            bufeteId = bufeteId,
            calificacionGeneral = request.calificacionGeneral,
            mensaje = request.mensaje
        )

        return calificacionBufeteRepository.create(calificacion)
    }
}