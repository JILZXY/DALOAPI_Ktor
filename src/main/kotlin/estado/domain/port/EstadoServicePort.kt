package com.example.estado.domain.port

import com.example.estado.domain.model.Estado

interface EstadoServicePort {
    suspend fun getAllEstados(): List<Estado>
    suspend fun getEstadoById(id: Int): Estado?
}