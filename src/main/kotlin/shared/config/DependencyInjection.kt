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
import com.example.usuario.application.UsuarioService
import com.example.usuario.application.AbogadoService
import com.example.usuario.domain.port.*
import com.example.usuario.infrastructure.persistence.UsuarioRepositoryAdapter
import com.example.usuario.infrastructure.persistence.AbogadoRepositoryAdapter
import com.example.usuario.infrastructure.web.UsuarioController
import com.example.usuario.infrastructure.web.AbogadoController
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

    //Usuario
    private val usuarioRepository: UsuarioRepositoryPort by lazy {
        UsuarioRepositoryAdapter(connection)
    }

    val usuarioService: UsuarioServicePort by lazy {
        UsuarioService(usuarioRepository, passwordHasher, jwtConfig)
    }

    val usuarioController: UsuarioController by lazy {
        UsuarioController(usuarioService)
    }

    //Abogado
    private val abogadoRepository: AbogadoRepositoryPort by lazy {
        AbogadoRepositoryAdapter(connection)
    }

    val abogadoService: AbogadoServicePort by lazy {
        AbogadoService(abogadoRepository)
    }

    val abogadoController: AbogadoController by lazy {
        AbogadoController(abogadoService)
    }
}