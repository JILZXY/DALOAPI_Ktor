package com.example.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Rol(
    val id: Int,
    val nombre: String
)