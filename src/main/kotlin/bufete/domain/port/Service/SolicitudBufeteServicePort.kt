package com.example.bufete.domain.port.Service

import com.example.bufete.domain.model.SolicitudBufete

interface SolicitudBufeteServicePort {
    suspend fun getAllSolicitudes(): List<SolicitudBufete>
    suspend fun getSolicitudById(id: Int): SolicitudBufete?
    suspend fun getSolicitudesByBufeteId(bufeteId: Int): List<SolicitudBufete>
    suspend fun getSolicitudesByAbogadoId(abogadoId: String): List<SolicitudBufete>
    suspend fun createSolicitud(abogadoId: String, bufeteId: Int): SolicitudBufete?
    suspend fun aprobarSolicitud(id: Int): Boolean
    suspend fun rechazarSolicitud(id: Int): Boolean
}