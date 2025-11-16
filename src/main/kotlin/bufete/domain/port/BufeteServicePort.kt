package com.example.bufete.domain.port

import com.example.bufete.domain.model.Bufete
import com.example.bufete.domain.model.CreateBufeteRequest

interface BufeteServicePort {
    suspend fun getAllBufetes(): List<Bufete>
    suspend fun getBufeteById(id: Int): Bufete?
    suspend fun getBufetesByAdminId(adminId: String): List<Bufete>
    suspend fun createBufete(adminId: String, request: CreateBufeteRequest): Bufete?
    suspend fun updateBufete(id: Int, bufete: Bufete): Bufete?
    suspend fun deleteBufete(id: Int): Boolean
}