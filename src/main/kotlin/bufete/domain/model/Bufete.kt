package com.example.bufete.domain.model
import com.example.shared.domain.model.Especialidad
import com.example.usuario.domain.model.Abogado
import kotlinx.serialization.Serializable

@Serializable
data class Bufete(
    val id: Int = 0,
    val adminBufeteId: String,
    val nombre: String,
    val descripcion: String?,
    val logo: String?,
    val fechaCreacion: String = "",
    val calificacionPromedio: Double = 0.0, // Calculado desde abogados
    val abogados: List<Abogado> = emptyList(),
    val especialidades: List<Especialidad> = emptyList()
)