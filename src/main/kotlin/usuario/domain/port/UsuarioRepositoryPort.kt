package com.example.usuario.domain.port

import com.example.usuario.domain.model.Usuario
import java.util.UUID

interface UsuarioRepositoryPort {
    suspend fun findAll(): List<Usuario>
    suspend fun findById(id: String): Usuario?
    suspend fun findByEmail(email: String): Usuario?
    suspend fun create(usuario: Usuario, passwordHash: String): Usuario?
    suspend fun update(usuario: Usuario): Usuario?
    suspend fun delete(id: String): Boolean
}