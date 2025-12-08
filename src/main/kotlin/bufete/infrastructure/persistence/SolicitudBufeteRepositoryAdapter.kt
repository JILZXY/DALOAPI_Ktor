package com.example.bufete.infrastructure.persistence

import com.example.bufete.domain.model.SolicitudBufete
import com.example.bufete.domain.port.Repository.SolicitudBufeteRepositoryPort
import java.sql.Connection
import java.sql.ResultSet

class SolicitudBufeteRepositoryAdapter(
    private val connection: Connection
) : SolicitudBufeteRepositoryPort {

    override suspend fun findAll(): List<SolicitudBufete> {
        val solicitudes = mutableListOf<SolicitudBufete>()
        val statement = connection.prepareStatement(
            """
            SELECT sb.id, sb.abogado_id, sb.bufete_id, sb.estado,
                   sb.fecha_solicitud, sb.fecha_aceptacion
            FROM Solicitudes_Bufete sb
            ORDER BY sb.fecha_solicitud DESC
            """
        )

        val resultSet = statement.executeQuery()
        while (resultSet.next()) {
            solicitudes.add(resultSet.toSolicitudBufete())
        }

        resultSet.close()
        statement.close()

        return solicitudes
    }

    override suspend fun findById(id: Int): SolicitudBufete? {
        val statement = connection.prepareStatement(
            """
            SELECT sb.id, sb.abogado_id, sb.bufete_id, sb.estado,
                   sb.fecha_solicitud, sb.fecha_aceptacion
            FROM Solicitudes_Bufete sb
            WHERE sb.id = ?
            """
        )
        statement.setInt(1, id)

        val resultSet = statement.executeQuery()
        val solicitud = if (resultSet.next()) {
            resultSet.toSolicitudBufete()
        } else {
            null
        }

        resultSet.close()
        statement.close()

        return solicitud
    }

    override suspend fun findByBufeteId(bufeteId: Int): List<SolicitudBufete> {
        val solicitudes = mutableListOf<SolicitudBufete>()
        val statement = connection.prepareStatement(
            """
            SELECT sb.id, sb.abogado_id, sb.bufete_id, sb.estado,
                   sb.fecha_solicitud, sb.fecha_aceptacion
            FROM Solicitudes_Bufete sb
            WHERE sb.bufete_id = ?
            ORDER BY sb.fecha_solicitud DESC
            """
        )
        statement.setInt(1, bufeteId)

        val resultSet = statement.executeQuery()
        while (resultSet.next()) {
            solicitudes.add(resultSet.toSolicitudBufete())
        }

        resultSet.close()
        statement.close()

        return solicitudes
    }

    override suspend fun findByAbogadoId(abogadoId: String): List<SolicitudBufete> {
        val solicitudes = mutableListOf<SolicitudBufete>()
        val statement = connection.prepareStatement(
            """
            SELECT sb.id, sb.abogado_id, sb.bufete_id, sb.estado,
                   sb.fecha_solicitud, sb.fecha_aceptacion
            FROM Solicitudes_Bufete sb
            WHERE sb.abogado_id = ?::uuid
            ORDER BY sb.fecha_solicitud DESC
            """
        )
        statement.setString(1, abogadoId)

        val resultSet = statement.executeQuery()
        while (resultSet.next()) {
            solicitudes.add(resultSet.toSolicitudBufete())
        }

        resultSet.close()
        statement.close()

        return solicitudes
    }

    override suspend fun create(solicitud: SolicitudBufete): SolicitudBufete? {
        val statement = connection.prepareStatement(
            """
            INSERT INTO Solicitudes_Bufete (abogado_id, bufete_id, estado, fecha_solicitud)
            VALUES (?::uuid, ?, ?::estado_solicitud, CURRENT_TIMESTAMP)
            RETURNING id, fecha_solicitud
            """
        )

        statement.setString(1, solicitud.abogadoId)
        statement.setInt(2, solicitud.bufeteId)
        statement.setString(3, solicitud.estado)

        val resultSet = statement.executeQuery()
        val created = if (resultSet.next()) {
            solicitud.copy(
                id = resultSet.getInt("id"),
                fechaSolicitud = resultSet.getTimestamp("fecha_solicitud").toString()
            )
        } else {
            null
        }

        resultSet.close()
        statement.close()

        return created
    }

    override suspend fun updateEstado(id: Int, estado: String): Boolean {
        val statement = if (estado == "Aprobado") {
            connection.prepareStatement(
                """
                UPDATE Solicitudes_Bufete 
                SET estado = ?::estado_solicitud, fecha_aceptacion = CURRENT_TIMESTAMP
                WHERE id = ?
                """
            )
        } else {
            connection.prepareStatement(
                """
                UPDATE Solicitudes_Bufete 
                SET estado = ?::estado_solicitud
                WHERE id = ?
                """
            )
        }

        statement.setString(1, estado)
        statement.setInt(2, id)

        val rowsAffected = statement.executeUpdate()
        statement.close()

        return rowsAffected > 0
    }

    override suspend fun deleteByAbogadoAndBufete(abogadoId: String, bufeteId: Int): Boolean {
        val statement = connection.prepareStatement(
            """
            DELETE FROM Solicitudes_Bufete
            WHERE abogado_id = ?::uuid AND bufete_id = ? AND estado = 'Aprobado'
            """
        )
        statement.setString(1, abogadoId)
        statement.setInt(2, bufeteId)

        val rowsAffected = statement.executeUpdate()
        statement.close()

        return rowsAffected > 0
    }

    private fun ResultSet.toSolicitudBufete(): SolicitudBufete {
        return SolicitudBufete(
            id = getInt("id"),
            abogadoId = getString("abogado_id"),
            bufeteId = getInt("bufete_id"),
            estado = getString("estado"),
            fechaSolicitud = getTimestamp("fecha_solicitud").toString(),
            fechaAceptacion = getTimestamp("fecha_aceptacion")?.toString()
        )
    }
}