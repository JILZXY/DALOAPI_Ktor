package com.example.bufete.application

import com.example.bufete.domain.model.Bufete
import com.example.bufete.domain.model.CreateBufeteRequest
import com.example.bufete.domain.port.BufeteServicePort
import com.example.bufete.domain.port.BufeteRepositoryPort

class BufeteService(
    private val bufeteRepository: BufeteRepositoryPort
) : BufeteServicePort {

    override suspend fun getAllBufetes(): List<Bufete> {
        return bufeteRepository.findAll()
    }

    override suspend fun getBufeteById(id: Int): Bufete? {
        return bufeteRepository.findById(id)
    }

    override suspend fun getBufetesByAdminId(adminId: String): List<Bufete> {
        return bufeteRepository.findByAdminId(adminId)
    }

    override suspend fun createBufete(adminId: String, request: CreateBufeteRequest): Bufete? {
        val bufete = Bufete(
            adminBufeteId = adminId,
            nombre = request.nombre,
            descripcion = request.descripcion,
            logo = request.logo
        )

        return bufeteRepository.create(bufete, request.especialidadesIds)
    }

    override suspend fun updateBufete(id: Int, bufete: Bufete): Bufete? {
        return bufeteRepository.update(bufete)
    }

    override suspend fun deleteBufete(id: Int): Boolean {
        return bufeteRepository.delete(id)
    }
}