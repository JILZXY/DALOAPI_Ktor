package com.example.consulta.domain.port.Service

import com.example.consulta.domain.model.Consulta
import com.example.consulta.domain.model.CreateConsultaRequest

interface ConsultaServicePort {
    suspend fun getAllConsultas(includePrivate: Boolean = false): List<Consulta>
    suspend fun getConsultaById(id: Int): Consulta?
    suspend fun getConsultasByUsuarioId(usuarioId: String): List<Consulta>
    suspend fun getConsultasByEspecialidad(especialidadId: Int): List<Consulta>
    suspend fun createConsulta(usuarioId: String, request: CreateConsultaRequest): Consulta?
    suspend fun updateEstadoConsulta(id: Int, estado: String): Boolean
    suspend fun deleteConsulta(id: Int): Boolean
    suspend fun getConsultasByMateria(materiaId: Int): List<Consulta>
    suspend fun getConsultasByLocalidad(estadoId: Int?, municipioId: Int?): List<Consulta>
    suspend fun getConsultasByMateriaYLocalidad(
        materiaId: Int,
        estadoId: Int?,
        municipioId: Int?
    ): List<Consulta>
    suspend fun getTotalConsultasByUsuarioId(usuarioId: String): Int
}