package com.example.usuario.domain.model

data class AbogadoConUsuario(
    val abogado: Abogado,
    val nombreUsuario: String,
    val email: String,
    val estadoId: Int?,
    val municipioId: Int?,
    val localidadId: Int?
)