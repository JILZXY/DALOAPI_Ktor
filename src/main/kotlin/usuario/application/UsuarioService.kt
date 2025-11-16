package com.example.usuario.application

import com.example.shared.security.JwtConfig
import com.example.shared.security.PasswordHasher
import com.example.usuario.domain.port.UsuarioRepositoryPort
import com.example.usuario.domain.port.UsuarioServicePort
import com.example.usuario.domain.model.*

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
        val token = jwtConfig.generateToken(createdUsuario.idUsuario, createdUsuario.email)

        return AuthResponse(token, createdUsuario)
    }

    override suspend fun login(request: LoginRequest): AuthResponse? {
        val usuario = usuarioRepository.findByEmail(request.email) ?: return null

        // Verificar contraseña (necesitarás obtener el hash de BD)
        // if (!passwordHasher.verify(request.contrasena, storedHash)) return null
        if (!usuario.activo) return null

        // Generar token
        val token = jwtConfig.generateToken(usuario.idUsuario, usuario.email)

        return AuthResponse(token, usuario)
    }

    override suspend fun updateUsuario(id: String, usuario: Usuario): Usuario? {
        return usuarioRepository.update(usuario)
    }

    override suspend fun deleteUsuario(id: String): Boolean {
        return usuarioRepository.delete(id)
    }
}