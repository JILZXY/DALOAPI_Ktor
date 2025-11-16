package com.example.estado.domain.port

import com.example.estado.domain.model.Estado

interface EstadoRepositoryPort {
    suspend fun findAll(): List<Estado>
    suspend fun findById(id: Int): Estado?
}
