package com.example.usuario.domain.port

import com.example.usuario.domain.model.Abogado

interface AbogadoRepositoryPort {
    suspend fun findAll(): List<Abogado>
    suspend fun findById(id: String): Abogado?
    suspend fun findByEspecialidad(especialidadId: Int): List<Abogado>
    suspend fun create(abogado: Abogado): Abogado?
    suspend fun update(abogado: Abogado): Abogado?
}
