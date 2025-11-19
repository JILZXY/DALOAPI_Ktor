package com.example.consulta.domain.port.Repository

import com.example.consulta.domain.model.Consulta

interface ConsultaRepositoryPort {
    suspend fun findAll(includePrivate: Boolean = false): List<Consulta>
    suspend fun findById(id: Int): Consulta?
    suspend fun findByUsuarioId(usuarioId: String): List<Consulta>
    suspend fun findByEspecialidad(especialidadId: Int): List<Consulta>
    suspend fun create(consulta: Consulta, especialidadesIds: List<Int>): Consulta?
    suspend fun updateEstado(id: Int, estado: String): Boolean
    suspend fun delete(id: Int): Boolean
    suspend fun findByMateria(materiaId: Int): List<Consulta>
    suspend fun findByLocalidad(estadoId: Int?, municipioId: Int?): List<Consulta>
    suspend fun findByMateriaYLocalidad(
        materiaId: Int,
        estadoId: Int?,
        municipioId: Int?
    ): List<Consulta>
    suspend fun countByUsuarioId(usuarioId: String): Int
}