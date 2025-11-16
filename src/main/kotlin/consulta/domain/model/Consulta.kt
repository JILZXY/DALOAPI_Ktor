package com.example.consulta.domain.model

import com.example.shared.domain.model.Especialidad
import com.example.usuario.domain.model.Usuario
import kotlinx.serialization.Serializable

@Serializable
data class Consulta(
    val idConsulta: Int = 0,
    val idUsuario: String,
    val titulo: String,
    val pregunta: String,
    val fechaPublicacion: String = "",
    val esPrivada: Boolean = false,
    val estado: String = "abierta",
    val usuario: Usuario? = null,
    val especialidades: List<Especialidad> = emptyList(),
    val respuestas: List<RespuestaConsulta> = emptyList()
)