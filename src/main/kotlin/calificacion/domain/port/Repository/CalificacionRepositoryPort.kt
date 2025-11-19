package com.example.calificacion.domain.port.Repository

import com.example.calificacion.domain.model.Calificacion
import com.example.calificacion.domain.model.CalificacionPromedio
import com.example.calificacion.domain.model.PromediosRespuesta

interface CalificacionRepositoryPort {
    suspend fun findByAbogadoId(abogadoId: String): List<Calificacion>
    suspend fun findById(id: Int): Calificacion?
    suspend fun create(calificacion: Calificacion): Calificacion?
    suspend fun getPromediosByAbogado(abogadoId: String): CalificacionPromedio
    suspend fun updateAbogadoCalificacionPromedio(abogadoId: String): Boolean
    suspend fun getPromediosByRespuestaId(respuestaId: Int): PromediosRespuesta
}