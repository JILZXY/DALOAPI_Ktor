package com.example.consulta.application

import com.example.consulta.domain.model.CreateRespuestaRequest
import com.example.consulta.domain.model.RespuestaConsulta
import com.example.consulta.domain.port.Repository.RespuestaConsultaRepositoryPort
import com.example.consulta.domain.port.Service.RespuestaConsultaServicePort

class RespuestaConsultaService(
    private val respuestaRepository: RespuestaConsultaRepositoryPort
) : RespuestaConsultaServicePort {

    override suspend fun getRespuestasByConsultaId(consultaId: Int): List<RespuestaConsulta> {
        return respuestaRepository.findByConsultaId(consultaId)
    }

    override suspend fun getRespuestaById(id: Int): RespuestaConsulta? {
        return respuestaRepository.findById(id)
    }

    override suspend fun createRespuesta(
        consultaId: Int,
        abogadoId: String,
        request: CreateRespuestaRequest
    ): RespuestaConsulta? {
        val respuesta = RespuestaConsulta(
            idConsulta = consultaId,
            idAbogado = abogadoId,
            respuesta = request.respuesta
        )

        return respuestaRepository.create(respuesta)
    }

    override suspend fun addLike(id: Int): Boolean {
        return respuestaRepository.addLike(id)
    }

    override suspend fun deleteRespuesta(id: Int): Boolean {
        return respuestaRepository.delete(id)
    }

    override suspend fun getTotalRespuestasByAbogadoId(abogadoId: String): Int {
        return respuestaRepository.countByAbogadoId(abogadoId)
    }

    override suspend fun getRespuestasByAbogadoId(abogadoId: String): List<RespuestaConsulta> {
        return respuestaRepository.findByAbogadoId(abogadoId)
    }
}