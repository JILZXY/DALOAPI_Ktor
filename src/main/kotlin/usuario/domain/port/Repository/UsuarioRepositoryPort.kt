package com.example.usuario.domain.port.Repository

import com.example.usuario.domain.model.Abogado
import com.example.usuario.domain.model.Usuario

interface UsuarioRepositoryPort {
    suspend fun findAll(): List<Usuario>
    suspend fun findById(id: String): Usuario?
    suspend fun findByEmail(email: String): Usuario?
    suspend fun create(usuario: Usuario, passwordHash: String): Usuario?
    suspend fun saveAbogadoCompleto(
        usuario: Usuario,
        abogado: Abogado,
        passwordHash: String,
        especialidadesIds: List<Int>
    ): Usuario?
    suspend fun update(usuario: Usuario): Usuario?
    suspend fun delete(id: String): Boolean
    suspend fun findInactivos(): List<Usuario>
    suspend fun activar(id: String): Boolean
}