package com.example.usuario.domain.port.Service

import com.example.usuario.domain.model.Abogado

interface AbogadoServicePort {
    suspend fun getAllAbogados(): List<Abogado>
    suspend fun getAbogadoById(id: String): Abogado?
    suspend fun getAbogadosByEspecialidad(especialidadId: Int): List<Abogado>
    suspend fun updateAbogado(id: String, abogado: Abogado): Abogado?
}