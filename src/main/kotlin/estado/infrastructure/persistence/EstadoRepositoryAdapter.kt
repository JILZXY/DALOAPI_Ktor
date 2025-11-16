package com.example.estado.infrastructure.persistence

import com.example.estado.domain.model.Estado
import com.example.estado.domain.port.EstadoRepositoryPort
import java.sql.Connection
import java.sql.ResultSet

class EstadoRepositoryAdapter(
    private val connection: Connection
) : EstadoRepositoryPort {

    override suspend fun findAll(): List<Estado> {
        val estados = mutableListOf<Estado>()
        val statement = connection.prepareStatement(
            "SELECT id, nombre FROM Estados ORDER BY nombre"
        )

        val resultSet = statement.executeQuery()
        while (resultSet.next()) {
            estados.add(resultSet.toEstado())
        }

        resultSet.close()
        statement.close()

        return estados
    }

    override suspend fun findById(id: Int): Estado? {
        val statement = connection.prepareStatement(
            "SELECT id, nombre FROM Estados WHERE id = ?"
        )
        statement.setInt(1, id)

        val resultSet = statement.executeQuery()
        val estado = if (resultSet.next()) {
            resultSet.toEstado()
        } else {
            null
        }

        resultSet.close()
        statement.close()

        return estado
    }

    private fun ResultSet.toEstado(): Estado {
        return Estado(
            id = getInt("id"),
            nombre = getString("nombre")
        )
    }
}