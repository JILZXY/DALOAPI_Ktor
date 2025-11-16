package com.example.usuario.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token: String,
    val usuario: Usuario
)