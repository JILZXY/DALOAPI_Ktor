package com.example.usuario.domain.model

import kotlinx.serialization.Serializable
import java.util.UUID
import com.example.municipio.domain.model.Municipio
import com.example.shared.domain.model.Rol

@Serializable
data class Usuario(
    val idUsuario: String,
    val nombre: String,
    val email: String,
    val fechaRegistro: String,
    val municipioId: Int?,
    val rolId: Int,
    val activo: Boolean,
    val municipio: Municipio? = null,
    val rol: Rol? = null
)
