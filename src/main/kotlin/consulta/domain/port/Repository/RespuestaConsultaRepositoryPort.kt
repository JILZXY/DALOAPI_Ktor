package com.example.consulta.domain.port.Repository

import com.example.consulta.domain.model.RespuestaConsulta

interface RespuestaConsultaRepositoryPort {
    suspend fun findByConsultaId(consultaId: Int): List<RespuestaConsulta>
    suspend fun findById(id: Int): RespuestaConsulta?
    suspend fun create(respuesta: RespuestaConsulta): RespuestaConsulta?
    suspend fun addLike(id: Int): Boolean
    suspend fun delete(id: Int): Boolean
    suspend fun countByAbogadoId(abogadoId: String): Int
    suspend fun findByAbogadoId(abogadoId: String): List<RespuestaConsulta>
}