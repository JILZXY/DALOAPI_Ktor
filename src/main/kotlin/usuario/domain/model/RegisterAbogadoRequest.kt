package com.example.usuario.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class RegisterAbogadoRequest(
    val nombre: String,
    val email: String,
    val contrasena: String,
    val municipioId: Int?,
    val cedulaProfesional: String,
    val biografia: String?,
    val especialidadesIds: List<Int>
)
