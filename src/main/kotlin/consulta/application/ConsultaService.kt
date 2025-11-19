package com.example.consulta.application

import com.example.consulta.domain.model.Consulta
import com.example.consulta.domain.model.CreateConsultaRequest
import com.example.consulta.domain.port.Repository.ConsultaRepositoryPort
import com.example.consulta.domain.port.Service.ConsultaServicePort

class ConsultaService(
    private val consultaRepository: ConsultaRepositoryPort
) : ConsultaServicePort {

    override suspend fun getAllConsultas(includePrivate: Boolean): List<Consulta> {
        return consultaRepository.findAll(includePrivate)
    }

    override suspend fun getConsultaById(id: Int): Consulta? {
        return consultaRepository.findById(id)
    }

    override suspend fun getConsultasByUsuarioId(usuarioId: String): List<Consulta> {
        return consultaRepository.findByUsuarioId(usuarioId)
    }

    override suspend fun getConsultasByEspecialidad(especialidadId: Int): List<Consulta> {
        return consultaRepository.findByEspecialidad(especialidadId)
    }

    override suspend fun createConsulta(usuarioId: String, request: CreateConsultaRequest): Consulta? {
        val consulta = Consulta(
            idUsuario = usuarioId,
            titulo = request.titulo,
            pregunta = request.pregunta,
            esPrivada = request.esPrivada,
            estado = "abierta"
        )

        return consultaRepository.create(consulta, request.especialidadesIds)
    }

    override suspend fun updateEstadoConsulta(id: Int, estado: String): Boolean {
        // Validar que el estado sea válido
        if (estado !in listOf("abierta", "atendida", "cerrada")) {
            return false
        }
        return consultaRepository.updateEstado(id, estado)
    }

    override suspend fun deleteConsulta(id: Int): Boolean {
        return consultaRepository.delete(id)
    }

    override suspend fun getConsultasByMateria(materiaId: Int): List<Consulta> {
        return consultaRepository.findByMateria(materiaId)
    }

    override suspend fun getConsultasByLocalidad(estadoId: Int?, municipioId: Int?): List<Consulta> {
        return consultaRepository.findByLocalidad(estadoId, municipioId)
    }

    override suspend fun getConsultasByMateriaYLocalidad(
        materiaId: Int,
        estadoId: Int?,
        municipioId: Int?
    ): List<Consulta> {
        return consultaRepository.findByMateriaYLocalidad(materiaId, estadoId, municipioId)
    }

    override suspend fun getTotalConsultasByUsuarioId(usuarioId: String): Int {
        return consultaRepository.countByUsuarioId(usuarioId)
    }
}
