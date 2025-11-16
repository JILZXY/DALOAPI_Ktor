package com.example.calificacion.domain.port

import com.example.calificacion.domain.model.Calificacion
import com.example.calificacion.domain.model.CalificacionPromedio
import com.example.calificacion.domain.model.CreateCalificacionRequest

interface CalificacionServicePort {
    suspend fun getCalificacionesByAbogadoId(abogadoId: String): List<Calificacion>
    suspend fun getCalificacionById(id: Int): Calificacion?
    suspend fun getPromediosByAbogado(abogadoId: String): CalificacionPromedio
    suspend fun createCalificacion(usuarioId: String, abogadoId: String, request: CreateCalificacionRequest): Calificacion?
}