package com.example.estado.application

import com.example.estado.domain.port.EstadoRepositoryPort
import com.example.estado.domain.port.EstadoServicePort
import com.example.estado.domain.model.Estado

class EstadoService(
    private val estadoRepository: EstadoRepositoryPort
) : EstadoServicePort {

    override suspend fun getAllEstados(): List<Estado> {
        return estadoRepository.findAll()
    }

    override suspend fun getEstadoById(id: Int): Estado? {
        return estadoRepository.findById(id)
    }
}