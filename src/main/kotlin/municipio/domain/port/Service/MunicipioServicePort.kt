package com.example.municipio.domain.port.Service

import com.example.municipio.domain.model.Municipio

interface MunicipioServicePort {
    suspend fun getAllMunicipios(): List<Municipio>
    suspend fun getMunicipioById(id: Int): Municipio?
    suspend fun getMunicipiosByEstadoId(estadoId: Int): List<Municipio>
}