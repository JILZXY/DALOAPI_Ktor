package com.example.bufete.domain.model

@Serializable
data class CreateBufeteRequest(
    val nombre: String,
    val descripcion: String?,
    val logo: String?,
    val especialidadesIds: List<Int>
)