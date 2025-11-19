package com.example.calificacion.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class PromediosRespuesta(
    val idRespuesta: Int,
    val atencionPromedio: Double,
    val profesionalismoPromedio: Double,
    val claridadPromedio: Double,
    val empatiaPromedio: Double,
    val promedioGeneral: Double,
    val totalCalificaciones: Int
)