package com.example.reporte.infrastructure.persistence

import com.example.reporte.domain.model.MotivoReporte
import com.example.reporte.domain.port.MotivoReporteRepositoryPort
import java.sql.Connection
import java.sql.ResultSet

class MotivoReporteRepositoryAdapter(
    private val connection: Connection
) : MotivoReporteRepositoryPort {

    override suspend fun findAll(): List<MotivoReporte> {
        val motivos = mutableListOf<MotivoReporte>()
        val statement = connection.prepareStatement(
            """
            SELECT id, nombre, descripcion
            FROM Catalogo_Motivos_Reporte
            ORDER BY nombre
            """
        )

        val resultSet = statement.executeQuery()
        while (resultSet.next()) {
            motivos.add(resultSet.toMotivoReporte())
        }

        resultSet.close()
        statement.close()

        return motivos
    }

    override suspend fun findById(id: Int): MotivoReporte? {
        val statement = connection.prepareStatement(
            """
            SELECT id, nombre, descripcion
            FROM Catalogo_Motivos_Reporte
            WHERE id = ?
            """
        )
        statement.setInt(1, id)

        val resultSet = statement.executeQuery()
        val motivo = if (resultSet.next()) {
            resultSet.toMotivoReporte()
        } else {
            null
        }

        resultSet.close()
        statement.close()

        return motivo
    }

    private fun ResultSet.toMotivoReporte(): MotivoReporte {
        return MotivoReporte(
            id = getInt("id"),
            nombre = getString("nombre"),
            descripcion = getString("descripcion")
        )
    }
}