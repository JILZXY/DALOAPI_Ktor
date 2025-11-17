package com.example.municipio.infrastructure.persistence

import com.example.estado.domain.model.Estado
import com.example.municipio.domain.model.Municipio
import com.example.municipio.domain.port.Repository.MunicipioRepositoryPort
import java.sql.Connection
import java.sql.ResultSet

class MunicipioRepositoryAdapter(
    private val connection: Connection
) : MunicipioRepositoryPort {

    override suspend fun findAll(): List<Municipio> {
        val municipios = mutableListOf<Municipio>()
        val statement = connection.prepareStatement(
            """
            SELECT 
                m.id, 
                m.nombre, 
                m.estado_id,
                e.nombre as estado_nombre
            FROM Municipios m
            INNER JOIN Estados e ON m.estado_id = e.id
            ORDER BY e.nombre, m.nombre
            """
        )

        val resultSet = statement.executeQuery()
        while (resultSet.next()) {
            municipios.add(resultSet.toMunicipio())
        }

        resultSet.close()
        statement.close()

        return municipios
    }

    override suspend fun findById(id: Int): Municipio? {
        val statement = connection.prepareStatement(
            """
            SELECT 
                m.id, 
                m.nombre, 
                m.estado_id,
                e.nombre as estado_nombre
            FROM Municipios m
            INNER JOIN Estados e ON m.estado_id = e.id
            WHERE m.id = ?
            """
        )
        statement.setInt(1, id)

        val resultSet = statement.executeQuery()
        val municipio = if (resultSet.next()) {
            resultSet.toMunicipio()
        } else {
            null
        }

        resultSet.close()
        statement.close()

        return municipio
    }

    override suspend fun findByEstadoId(estadoId: Int): List<Municipio> {
        val municipios = mutableListOf<Municipio>()
        val statement = connection.prepareStatement(
            """
            SELECT 
                m.id, 
                m.nombre, 
                m.estado_id,
                e.nombre as estado_nombre
            FROM Municipios m
            INNER JOIN Estados e ON m.estado_id = e.id
            WHERE m.estado_id = ?
            ORDER BY m.nombre
            """
        )
        statement.setInt(1, estadoId)

        val resultSet = statement.executeQuery()
        while (resultSet.next()) {
            municipios.add(resultSet.toMunicipio())
        }

        resultSet.close()
        statement.close()

        return municipios
    }

    private fun ResultSet.toMunicipio(): Municipio {
        return Municipio(
            id = getInt("id"),
            nombre = getString("nombre"),
            estadoId = getInt("estado_id"),
            estado = Estado(
                id = getInt("estado_id"),
                nombre = getString("estado_nombre")
            )
        )
    }
}