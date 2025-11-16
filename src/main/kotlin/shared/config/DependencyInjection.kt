package com.example.shared.config

import com.example.estado.application.EstadoService
import com.example.estado.domain.port.EstadoServicePort
import com.example.estado.domain.port.EstadoRepositoryPort
import com.example.estado.infrastructure.persistence.EstadoRepositoryAdapter
import com.example.estado.infrastructure.web.EstadoController
import com.example.municipio.application.MunicipioService
import com.example.municipio.infrastructure.web.MunicipioController
import com.example.municipio.domain.port.MunicipioServicePort
import com.example.municipio.domain.port.MunicipioRepositoryPort
import com.example.municipio.infrastructure.persistence.MunicipioRepositoryAdapter
import com.example.shared.security.JwtConfig
import com.example.shared.security.PasswordHasher
import java.sql.Connection


object DependencyInjection {

    private val connection: Connection by lazy {
        DatabaseConfig.getConnection()
    }

    // Security
    val jwtConfig: JwtConfig by lazy {
        JwtConfig()
    }

    val passwordHasher: PasswordHasher by lazy {
        PasswordHasher()
    }
    // Estado
    private val estadoRepository: EstadoRepositoryPort by lazy {
        EstadoRepositoryAdapter(connection)
    }

    val estadoService: EstadoServicePort by lazy {
        EstadoService(estadoRepository)
    }

    val estadoController: EstadoController by lazy {
        EstadoController(estadoService)
    }

    //Municipio
    private val municipioRepository: MunicipioRepositoryPort by lazy {
        MunicipioRepositoryAdapter(connection)
    }

    val municipioService: MunicipioServicePort by lazy {
        MunicipioService(municipioRepository)
    }

    val municipioController: MunicipioController by lazy {
        MunicipioController(municipioService)
    }
}