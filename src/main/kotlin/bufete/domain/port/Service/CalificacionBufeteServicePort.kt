package com.example.bufete.domain.port.Service

import com.example.bufete.domain.model.CalificacionBufete
import com.example.bufete.domain.model.CreateCalificacionBufeteRequest

interface CalificacionBufeteServicePort {
    suspend fun getCalificacionesByBufeteId(bufeteId: Int): List<CalificacionBufete>
    suspend fun createCalificacion(usuarioId: String, bufeteId: Int, request: CreateCalificacionBufeteRequest): CalificacionBufete?
}