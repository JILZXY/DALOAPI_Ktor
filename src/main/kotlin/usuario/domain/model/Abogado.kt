package com.example.usuario.domain.model

import com.example.shared.domain.model.Especialidad
import kotlinx.serialization.Serializable

@Serializable
data class Abogado(
    val idUsuario: String,
    val cedulaProfesional: String?,
    val biografia: String?,
    val calificacionPromedio: Double,
    val usuario: Usuario? = null, // Información del usuario base
    val especialidades: List<Especialidad> = emptyList()
)