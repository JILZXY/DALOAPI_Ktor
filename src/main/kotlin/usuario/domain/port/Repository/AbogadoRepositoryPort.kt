package com.example.usuario.domain.port.Repository

import com.example.shared.domain.model.Especialidad
import com.example.usuario.domain.model.Abogado
import com.example.usuario.domain.model.AbogadoConUsuario

interface AbogadoRepositoryPort {
    suspend fun findAll(): List<Abogado>
    suspend fun findById(id: String): Abogado?
    suspend fun findByEspecialidad(especialidadId: Int): List<Abogado>
    suspend fun create(abogado: Abogado): Abogado?
    suspend fun update(abogado: Abogado): Abogado?
    suspend fun findActivosByNombre(nombre: String): List<Abogado>
    suspend fun findActivosByLocalidad(estadoId: Int?, municipioId: Int?): List<Abogado>
    suspend fun findActivosConFiltros(
        materiaId: Int?,
        estadoId: Int?,
        municipioId: Int?,
        ordenarPorCalificacion: Boolean
    ): List<Abogado>
    suspend fun getEspecialidadesByAbogadoId(abogadoId: String): List<Especialidad>
    suspend fun buscarPorEspecialidad(especialidadId: Int): List<AbogadoConUsuario>
    suspend fun buscarPorEstado(estadoId: Int): List<AbogadoConUsuario>
    suspend fun buscarPorMunicipio(municipioId: Int): List<AbogadoConUsuario>
}
