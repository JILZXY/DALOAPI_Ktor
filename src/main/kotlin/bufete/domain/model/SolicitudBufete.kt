package com.example.bufete.domain.model
import kotlinx.serialization.Serializable
import com.example.usuario.domain.model.Abogado

@Serializable
data class SolicitudBufete(
    val id: Int = 0,
    val abogadoId: String,
    val bufeteId: Int,
    val estado: String, // Pendiente, Aprobado, Rechazado
    val fechaSolicitud: String = "",
    val fechaAceptacion: String? = null,
    val abogado: Abogado? = null,
    val bufete: Bufete? = null
)