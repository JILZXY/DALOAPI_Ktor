package com.example.usuario.application

import com.example.usuario.domain.model.Abogado
import com.example.usuario.domain.port.AbogadoServicePort
import com.example.usuario.domain.port.AbogadoRepositoryPort

class AbogadoService(
    private val abogadoRepository: AbogadoRepositoryPort
) : AbogadoServicePort {

    override suspend fun getAllAbogados(): List<Abogado> {
        return abogadoRepository.findAll()
    }

    override suspend fun getAbogadoById(id: String): Abogado? {
        return abogadoRepository.findById(id)
    }

    override suspend fun getAbogadosByEspecialidad(especialidadId: Int): List<Abogado> {
        return abogadoRepository.findByEspecialidad(especialidadId)
    }

    override suspend fun updateAbogado(id: String, abogado: Abogado): Abogado? {
        return abogadoRepository.update(abogado)
    }
}