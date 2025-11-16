package com.example.bufete.domain.port

import com.example.bufete.domain.model.SolicitudBufete

interface SolicitudBufeteRepositoryPort {
    suspend fun findAll(): List<SolicitudBufete>
    suspend fun findById(id: Int): SolicitudBufete?
    suspend fun findByBufeteId(bufeteId: Int): List<SolicitudBufete>
    suspend fun findByAbogadoId(abogadoId: String): List<SolicitudBufete>
    suspend fun create(solicitud: SolicitudBufete): SolicitudBufete?
    suspend fun updateEstado(id: Int, estado: String): Boolean
}