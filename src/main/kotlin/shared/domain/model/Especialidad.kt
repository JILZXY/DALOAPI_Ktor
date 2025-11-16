package com.example.shared.domain.model
import kotlinx.serialization.Serializable

@Serializable
data class Especialidad(
    val id: Int,
    val nombreMateria: String,
    val descripcion: String?
)