package com.example.usuario.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val nombre: String,
    val email: String,
    val contrasena: String,
    val municipioId: Int?,
    val rolId: Int
)