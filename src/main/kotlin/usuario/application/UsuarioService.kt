package com.example.usuario.application

import com.example.shared.security.JwtConfig
import com.example.shared.security.PasswordHasher
import com.example.usuario.domain.port.Repository.UsuarioRepositoryPort
import com.example.usuario.domain.port.Service.UsuarioServicePort
import com.example.usuario.domain.model.*
import java.util.UUID

class UsuarioService(
    private val usuarioRepository: UsuarioRepositoryPort,
    private val passwordHasher: PasswordHasher,
    private val jwtConfig: JwtConfig
) : UsuarioServicePort {

    override suspend fun getAllUsuarios(): List<Usuario> {
        return usuarioRepository.findAll()
    }

    override suspend fun getUsuarioById(id: String): Usuario? {
        return usuarioRepository.findById(id)
    }

    override suspend fun register(request: RegisterRequest): AuthResponse? {
        // Verificar si el email ya existe
        if (usuarioRepository.findByEmail(request.email) != null) {
            return null // Email ya registrado
        }

        // Hash de la contraseña
        val passwordHash = passwordHasher.hash(request.contrasena)

        // Crear usuario
        val usuario = Usuario(
            idUsuario = "", // Se genera en BD
            nombre = request.nombre,
            email = request.email,
            fechaRegistro = "",
            municipioId = request.municipioId,
            rolId = request.rolId,
            activo = true
        )

        val createdUsuario = usuarioRepository.create(usuario, passwordHash) ?: return null

        // Generar token
        val token = jwtConfig.generateToken(createdUsuario.idUsuario, createdUsuario.email, createdUsuario.rolId)

        return AuthResponse(token, createdUsuario)
    }

    override suspend fun registerAbogado(request: RegisterAbogadoRequest): AuthResponse? {
        if (usuarioRepository.findByEmail(request.email) != null) {
            return null
        }

        val passwordHash = passwordHasher.hash(request.contrasena)
        val userId = UUID.randomUUID().toString()

        val usuario = Usuario(
            idUsuario = userId,
            nombre = request.nombre,
            email = request.email,
            fechaRegistro = "",
            municipioId = request.municipioId,
            rolId = 2, // Rol de Abogado
            activo = false // Inactivo hasta validación
        )

        val abogado = Abogado(
            idUsuario = userId,
            cedulaProfesional = request.cedulaProfesional,
            biografia = request.biografia,
            calificacionPromedio = 0.0
        )

        val createdUsuario = usuarioRepository.saveAbogadoCompleto(usuario, passwordHash, abogado, request.especialidadesIds) ?: return null

        val token = jwtConfig.generateToken(createdUsuario.idUsuario, createdUsuario.email, createdUsuario.rolId)

        return AuthResponse(token, createdUsuario)
    }

    override suspend fun login(request: LoginRequest): AuthResponse? {
        val usuario = usuarioRepository.findByEmail(request.email) ?: return null

        // Verificar contraseña (necesitarás obtener el hash de BD)
        if (!usuario.activo) return null

        // Generar token
        val token = jwtConfig.generateToken(usuario.idUsuario, usuario.email, usuario.rolId)

        return AuthResponse(token, usuario)
    }

    override suspend fun updateUsuario(id: String, usuario: Usuario): Usuario? {
        return usuarioRepository.update(usuario)
    }

    override suspend fun deleteUsuario(id: String): Boolean {
        return usuarioRepository.delete(id)
    }

    override suspend fun getUsuariosInactivos(): List<Usuario> {
        return usuarioRepository.findInactivos()
    }

    override suspend fun activarUsuario(id: String): Boolean {
        return usuarioRepository.activar(id)
    }

    override suspend fun getUsuarioByEmail(email: String): Usuario? {
        return usuarioRepository.findByEmail(email)
    }
}