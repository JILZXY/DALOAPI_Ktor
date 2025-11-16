package com.example.calificacion.domain.model

import com.example.usuario.domain.model.Abogado
import com.example.usuario.domain.model.Usuario
import kotlinx.serialization.Serializable

@Serializable
data class Calificacion(
    val idCalificacion: Int = 0,
    val idUsuario: String,
    val idAbogado: String,
    val atencion: Int,
    val profesionalismo: Int,
    val claridad: Int,
    val empatia: Int,
    val comentarioOpcional: String?,
    val fecha: String = "",
    val usuario: Usuario? = null,
    val abogado: Abogado? = null
)