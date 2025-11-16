package com.example.calificacion.application

import com.example.calificacion.domain.model.Calificacion
import com.example.calificacion.domain.model.CalificacionPromedio
import com.example.calificacion.domain.model.CreateCalificacionRequest
import com.example.calificacion.domain.port.CalificacionServicePort
import com.example.calificacion.domain.port.CalificacionRepositoryPort

class CalificacionService(
    private val calificacionRepository: CalificacionRepositoryPort
) : CalificacionServicePort {

    override suspend fun getCalificacionesByAbogadoId(abogadoId: String): List<Calificacion> {
        return calificacionRepository.findByAbogadoId(abogadoId)
    }

    override suspend fun getCalificacionById(id: Int): Calificacion? {
        return calificacionRepository.findById(id)
    }

    override suspend fun getPromediosByAbogado(abogadoId: String): CalificacionPromedio {
        return calificacionRepository.getPromediosByAbogado(abogadoId)
    }

    override suspend fun createCalificacion(
        usuarioId: String,
        abogadoId: String,
        request: CreateCalificacionRequest
    ): Calificacion? {
        // Validar que las calificaciones estén entre 1 y 5
        if (request.atencion !in 1..5 || request.profesionalismo !in 1..5 ||
            request.claridad !in 1..5 || request.empatia !in 1..5) {
            return null
        }

        val calificacion = Calificacion(
            idUsuario = usuarioId,
            idAbogado = abogadoId,
            atencion = request.atencion,
            profesionalismo = request.profesionalismo,
            claridad = request.claridad,
            empatia = request.empatia,
            comentarioOpcional = request.comentarioOpcional
        )

        val created = calificacionRepository.create(calificacion)

        // Actualizar promedio del abogado
        if (created != null) {
            calificacionRepository.updateAbogadoCalificacionPromedio(abogadoId)
        }

        return created
    }
}