package com.example.municipio.application

import com.example.municipio.domain.port.MunicipioRepositoryPort
import com.example.municipio.domain.port.MunicipioServicePort
import com.example.municipio.domain.model.Municipio

class MunicipioService(
    private val municipioRepository: MunicipioRepositoryPort
) : MunicipioServicePort {

    override suspend fun getAllMunicipios(): List<Municipio> {
        return municipioRepository.findAll()
    }

    override suspend fun getMunicipioById(id: Int): Municipio? {
        return municipioRepository.findById(id)
    }

    override suspend fun getMunicipiosByEstadoId(estadoId: Int): List<Municipio> {
        return municipioRepository.findByEstadoId(estadoId)
    }
}