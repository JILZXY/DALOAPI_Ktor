package com.example.bufete.domain.port.Repository

import com.example.bufete.domain.model.CalificacionBufete

interface CalificacionBufeteRepositoryPort {
    suspend fun findByBufeteId(bufeteId: Int): List<CalificacionBufete>
    suspend fun create(calificacion: CalificacionBufete): CalificacionBufete?
}
