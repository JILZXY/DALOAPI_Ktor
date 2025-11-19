package com.example.calificacion.domain.port.Service

import com.example.calificacion.domain.model.Calificacion
import com.example.calificacion.domain.model.CalificacionPromedio
import com.example.calificacion.domain.model.CreateCalificacionRequest
import com.example.calificacion.domain.model.PromediosRespuesta

interface CalificacionServicePort {
    suspend fun getCalificacionesByAbogadoId(abogadoId: String): List<Calificacion>
    suspend fun getCalificacionById(id: Int): Calificacion?
    suspend fun getPromediosByAbogado(abogadoId: String): CalificacionPromedio
    suspend fun createCalificacion(usuarioId: String, abogadoId: String, request: CreateCalificacionRequest): Calificacion?
    suspend fun getPromedioGeneralAbogado(abogadoId: String): Double
    suspend fun getPromediosPorRespuesta(respuestaId: Int): PromediosRespuesta
}