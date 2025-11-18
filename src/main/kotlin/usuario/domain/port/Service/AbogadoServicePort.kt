package com.example.usuario.domain.port.Service

import com.example.shared.domain.model.Especialidad
import com.example.usuario.domain.model.Abogado

interface AbogadoServicePort {
    suspend fun getAllAbogados(): List<Abogado>
    suspend fun getAbogadoById(id: String): Abogado?
    suspend fun getAbogadosByEspecialidad(especialidadId: Int): List<Abogado>
    suspend fun updateAbogado(id: String, abogado: Abogado): Abogado?
    suspend fun buscarAbogadosPorNombre(nombre: String): List<Abogado>
    suspend fun getAbogadosByLocalidad(estadoId: Int?, municipioId: Int?): List<Abogado>
    suspend fun filtrarAbogados(
        materiaId: Int?,
        estadoId: Int?,
        municipioId: Int?,
        ordenarPorCalificacion: Boolean
    ): List<Abogado>
    suspend fun getEspecialidadesByAbogadoId(abogadoId: String): List<Especialidad>
}