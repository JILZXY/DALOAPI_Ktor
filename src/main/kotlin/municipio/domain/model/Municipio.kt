package com.example.municipio.domain.model

import kotlinx.serialization.Serializable
import com.example.estado.domain.model.Estado

@Serializable
data class Municipio(
    val id: Int,
    val nombre: String,
    val estadoId: Int,
    val estado: Estado? = null
)