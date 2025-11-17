package com.example.consulta.domain.port.Service

import com.example.consulta.domain.model.CreateRespuestaRequest
import com.example.consulta.domain.model.RespuestaConsulta

interface RespuestaConsultaServicePort {
    suspend fun getRespuestasByConsultaId(consultaId: Int): List<RespuestaConsulta>
    suspend fun getRespuestaById(id: Int): RespuestaConsulta?
    suspend fun createRespuesta(consultaId: Int, abogadoId: String, request: CreateRespuestaRequest): RespuestaConsulta?
    suspend fun addLike(id: Int): Boolean
    suspend fun deleteRespuesta(id: Int): Boolean
}