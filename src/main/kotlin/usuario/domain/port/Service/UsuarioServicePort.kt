package com.example.usuario.domain.port.Service

import com.example.usuario.domain.model.AuthResponse
import com.example.usuario.domain.model.LoginRequest
import com.example.usuario.domain.model.RegisterRequest
import com.example.usuario.domain.model.Usuario

interface UsuarioServicePort {
    suspend fun getAllUsuarios(): List<Usuario>
    suspend fun getUsuarioById(id: String): Usuario?
    suspend fun register(request: RegisterRequest): AuthResponse?
    suspend fun login(request: LoginRequest): AuthResponse?
    suspend fun updateUsuario(id: String, usuario: Usuario): Usuario?
    suspend fun deleteUsuario(id: String): Boolean
    suspend fun getUsuariosInactivos(): List<Usuario>
    suspend fun activarUsuario(id: String): Boolean
    suspend fun getUsuarioByEmail(email: String): Usuario?
}