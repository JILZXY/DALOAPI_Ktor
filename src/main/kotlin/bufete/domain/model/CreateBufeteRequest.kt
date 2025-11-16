package com.example.bufete.domain.model
import kotlinx.serialization.Serializable
@Serializable
data class CreateBufeteRequest(
    val nombre: String,
    val descripcion: String?,
    val logo: String?,
    val especialidadesIds: List<Int>
)