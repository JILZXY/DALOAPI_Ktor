package com.example.bufete.domain.port.Repository

import com.example.bufete.domain.model.Bufete
import com.example.usuario.domain.model.Abogado

interface BufeteRepositoryPort {
    suspend fun findAll(): List<Bufete>
    suspend fun findById(id: Int): Bufete?
    suspend fun findByAdminId(adminId: String): List<Bufete>
    suspend fun create(bufete: Bufete, especialidadesIds: List<Int>): Bufete?
    suspend fun update(bufete: Bufete): Bufete?
    suspend fun delete(id: Int): Boolean
    suspend fun calculateCalificacionPromedio(bufeteId: Int): Double
    suspend fun findAbogadosByBufeteYEspecialidad(bufeteId: Int, especialidadId: Int): List<Abogado>
}