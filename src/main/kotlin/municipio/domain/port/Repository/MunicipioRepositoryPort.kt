package com.example.municipio.domain.port.Repository

import com.example.municipio.domain.model.Municipio

interface MunicipioRepositoryPort {
    suspend fun findAll(): List<Municipio>
    suspend fun findById(id: Int): Municipio?
    suspend fun findByEstadoId(estadoId: Int): List<Municipio>
}