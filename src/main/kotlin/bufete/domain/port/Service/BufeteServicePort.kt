package com.example.bufete.domain.port.Service

import com.example.bufete.domain.model.Bufete
import com.example.bufete.domain.model.CreateBufeteRequest
import com.example.usuario.domain.model.Abogado

interface BufeteServicePort {
    suspend fun getAllBufetes(): List<Bufete>
    suspend fun getBufeteById(id: Int): Bufete?
    suspend fun getBufetesByAdminId(adminId: String): List<Bufete>
    suspend fun createBufete(adminId: String, request: CreateBufeteRequest): Bufete?
    suspend fun updateBufete(id: Int, bufete: Bufete): Bufete?
    suspend fun deleteBufete(id: Int): Boolean
    suspend fun getAbogadosByBufeteYEspecialidad(bufeteId: Int, especialidadId: Int): List<Abogado>
}