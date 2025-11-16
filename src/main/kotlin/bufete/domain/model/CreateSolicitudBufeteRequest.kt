package com.example.bufete.domain.model
import kotlinx.serialization.Serializable
@Serializable
data class CreateSolicitudBufeteRequest(
    val bufeteId: Int
)