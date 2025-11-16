package com.example.shared.config

import com.example.estado.application.EstadoService
import com.example.estado.domain.port.EstadoServicePort
import com.example.estado.domain.port.EstadoRepositoryPort
import com.example.estado.infrastructure.persistence.EstadoRepositoryAdapter
import com.example.estado.infrastructure.web.EstadoController

import java.sql.Connection


object DependencyInjection {

    private val connection: Connection by lazy {
        DatabaseConfig.getConnection()
    }

    private val estadoRepository: EstadoRepositoryPort by lazy {
        EstadoRepositoryAdapter(connection)
    }

    val estadoService: EstadoServicePort by lazy {
        EstadoService(estadoRepository)
    }

    val estadoController: EstadoController by lazy {
        EstadoController(estadoService)
    }
}