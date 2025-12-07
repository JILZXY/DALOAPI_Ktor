package com.example.usuario.application

import com.example.usuario.domain.model.Abogado
import com.example.usuario.domain.port.Service.AbogadoServicePort
import com.example.usuario.domain.port.Repository.AbogadoRepositoryPort
import com.example.shared.domain.model.Especialidad
import com.example.usuario.domain.model.AbogadoConUsuario
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

    override suspend fun buscarAbogadosPorNombre(nombre: String): List<Abogado> {
        return abogadoRepository.findActivosByNombre(nombre)
    }

    override suspend fun getAbogadosByLocalidad(estadoId: Int?, municipioId: Int?): List<Abogado> {
        return abogadoRepository.findActivosByLocalidad(estadoId, municipioId)
    }

    override suspend fun filtrarAbogados(
        materiaId: Int?,
        estadoId: Int?,
        municipioId: Int?,
        ordenarPorCalificacion: Boolean
    ): List<Abogado> {
        return abogadoRepository.findActivosConFiltros(
            materiaId,
            estadoId,
            municipioId,
            ordenarPorCalificacion
        )
    }

    override suspend fun getEspecialidadesByAbogadoId(abogadoId: String): List<Especialidad> {
        return abogadoRepository.getEspecialidadesByAbogadoId(abogadoId)
    }

    override suspend fun buscarPorEspecialidad(especialidadId: Int): List<AbogadoConUsuario> {
        return abogadoRepository.buscarPorEspecialidad(especialidadId)
    }

    override suspend fun buscarPorEstado(estadoId: Int): List<AbogadoConUsuario> {
        return abogadoRepository.buscarPorEstado(estadoId)
    }

    override suspend fun buscarPorMunicipio(municipioId: Int): List<AbogadoConUsuario> {
        return abogadoRepository.buscarPorMunicipio(municipioId)
    }
}