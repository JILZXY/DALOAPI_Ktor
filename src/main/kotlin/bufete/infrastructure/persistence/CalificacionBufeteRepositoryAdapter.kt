package com.example.bufete.infrastructure.persistence

import com.example.bufete.domain.model.CalificacionBufete
import com.example.bufete.domain.port.CalificacionBufeteRepositoryPort
import java.sql.Connection
import java.sql.ResultSet

class CalificacionBufeteRepositoryAdapter(
    private val connection: Connection
) : CalificacionBufeteRepositoryPort {

    override suspend fun findByBufeteId(bufeteId: Int): List<CalificacionBufete> {
        val calificaciones = mutableListOf<CalificacionBufete>()
        val statement = connection.prepareStatement(
            """
            SELECT cb.id, cb.usuario_cliente_id, cb.bufete_id, 
                   cb.calificacion_general, cb.mensaje, cb.fecha_calificacion
            FROM Calificacion_Bufete cb
            WHERE cb.bufete_id = ?
            ORDER BY cb.fecha_calificacion DESC
            """
        )
        statement.setInt(1, bufeteId)

        val resultSet = statement.executeQuery()
        while (resultSet.next()) {
            calificaciones.add(resultSet.toCalificacionBufete())
        }

        resultSet.close()
        statement.close()

        return calificaciones
    }

    override suspend fun create(calificacion: CalificacionBufete): CalificacionBufete? {
        val statement = connection.prepareStatement(
            """
            INSERT INTO Calificacion_Bufete (usuario_cliente_id, bufete_id, calificacion_general, mensaje, fecha_calificacion)
            VALUES (?::uuid, ?, ?, ?, CURRENT_TIMESTAMP)
            RETURNING id, fecha_calificacion
            """
        )

        statement.setString(1, calificacion.usuarioClienteId)
        statement.setInt(2, calificacion.bufeteId)
        statement.setDouble(3, calificacion.calificacionGeneral)
        statement.setString(4, calificacion.mensaje)

        val resultSet = statement.executeQuery()
        val created = if (resultSet.next()) {
            calificacion.copy(
                id = resultSet.getInt("id"),
                fechaCalificacion = resultSet.getTimestamp("fecha_calificacion").toString()
            )
        } else {
            null
        }

        resultSet.close()
        statement.close()

        return created
    }

    private fun ResultSet.toCalificacionBufete(): CalificacionBufete {
        return CalificacionBufete(
            id = getInt("id"),
            usuarioClienteId = getString("usuario_cliente_id"),
            bufeteId = getInt("bufete_id"),
            calificacionGeneral = getDouble("calificacion_general"),
            mensaje = getString("mensaje"),
            fechaCalificacion = getTimestamp("fecha_calificacion").toString()
        )
    }
}