package com.example.reporte.infrastructure.persistence

import com.example.reporte.domain.model.MotivoReporte
import com.example.reporte.domain.model.Reporte
import com.example.reporte.domain.port.ReporteRepositoryPort
import com.example.usuario.domain.model.Usuario
import java.sql.Connection
import java.sql.ResultSet

class ReporteRepositoryAdapter(
    private val connection: Connection
) : ReporteRepositoryPort {

    override suspend fun findAll(): List<Reporte> {
        val reportes = mutableListOf<Reporte>()
        val statement = connection.prepareStatement(
            """
            SELECT r.id, r.usuario_reporta_id, r.usuario_reportado_id,
                   r.motivo_reporte_id, r.consulta_id, r.fecha_reporte, r.comentarios,
                   ur.nombre as reporta_nombre, ur.email as reporta_email,
                   urd.nombre as reportado_nombre, urd.email as reportado_email,
                   mr.nombre as motivo_nombre, mr.descripcion as motivo_descripcion
            FROM Reportes r
            INNER JOIN Usuarios ur ON r.usuario_reporta_id = ur.id_usuario
            INNER JOIN Usuarios urd ON r.usuario_reportado_id = urd.id_usuario
            INNER JOIN Catalogo_Motivos_Reporte mr ON r.motivo_reporte_id = mr.id
            ORDER BY r.fecha_reporte DESC
            """
        )

        val resultSet = statement.executeQuery()
        while (resultSet.next()) {
            reportes.add(resultSet.toReporte())
        }

        resultSet.close()
        statement.close()

        return reportes
    }

    override suspend fun findById(id: Int): Reporte? {
        val statement = connection.prepareStatement(
            """
            SELECT r.id, r.usuario_reporta_id, r.usuario_reportado_id,
                   r.motivo_reporte_id, r.consulta_id, r.fecha_reporte, r.comentarios,
                   ur.nombre as reporta_nombre, ur.email as reporta_email,
                   urd.nombre as reportado_nombre, urd.email as reportado_email,
                   mr.nombre as motivo_nombre, mr.descripcion as motivo_descripcion
            FROM Reportes r
            INNER JOIN Usuarios ur ON r.usuario_reporta_id = ur.id_usuario
            INNER JOIN Usuarios urd ON r.usuario_reportado_id = urd.id_usuario
            INNER JOIN Catalogo_Motivos_Reporte mr ON r.motivo_reporte_id = mr.id
            WHERE r.id = ?
            """
        )
        statement.setInt(1, id)

        val resultSet = statement.executeQuery()
        val reporte = if (resultSet.next()) {
            resultSet.toReporte()
        } else {
            null
        }

        resultSet.close()
        statement.close()

        return reporte
    }

    override suspend fun findByUsuarioReportadoId(usuarioId: String): List<Reporte> {
        val reportes = mutableListOf<Reporte>()
        val statement = connection.prepareStatement(
            """
            SELECT r.id, r.usuario_reporta_id, r.usuario_reportado_id,
                   r.motivo_reporte_id, r.consulta_id, r.fecha_reporte, r.comentarios,
                   ur.nombre as reporta_nombre, ur.email as reporta_email,
                   urd.nombre as reportado_nombre, urd.email as reportado_email,
                   mr.nombre as motivo_nombre, mr.descripcion as motivo_descripcion
            FROM Reportes r
            INNER JOIN Usuarios ur ON r.usuario_reporta_id = ur.id_usuario
            INNER JOIN Usuarios urd ON r.usuario_reportado_id = urd.id_usuario
            INNER JOIN Catalogo_Motivos_Reporte mr ON r.motivo_reporte_id = mr.id
            WHERE r.usuario_reportado_id = ?::uuid
            ORDER BY r.fecha_reporte DESC
            """
        )
        statement.setString(1, usuarioId)

        val resultSet = statement.executeQuery()
        while (resultSet.next()) {
            reportes.add(resultSet.toReporte())
        }

        resultSet.close()
        statement.close()

        return reportes
    }

    override suspend fun findByUsuarioReportaId(usuarioId: String): List<Reporte> {
        val reportes = mutableListOf<Reporte>()
        val statement = connection.prepareStatement(
            """
            SELECT r.id, r.usuario_reporta_id, r.usuario_reportado_id,
                   r.motivo_reporte_id, r.consulta_id, r.fecha_reporte, r.comentarios,
                   ur.nombre as reporta_nombre, ur.email as reporta_email,
                   urd.nombre as reportado_nombre, urd.email as reportado_email,
                   mr.nombre as motivo_nombre, mr.descripcion as motivo_descripcion
            FROM Reportes r
            INNER JOIN Usuarios ur ON r.usuario_reporta_id = ur.id_usuario
            INNER JOIN Usuarios urd ON r.usuario_reportado_id = urd.id_usuario
            INNER JOIN Catalogo_Motivos_Reporte mr ON r.motivo_reporte_id = mr.id
            WHERE r.usuario_reporta_id = ?::uuid
            ORDER BY r.fecha_reporte DESC
            """
        )
        statement.setString(1, usuarioId)

        val resultSet = statement.executeQuery()
        while (resultSet.next()) {
            reportes.add(resultSet.toReporte())
        }

        resultSet.close()
        statement.close()

        return reportes
    }

    override suspend fun create(reporte: Reporte): Reporte? {
        val statement = connection.prepareStatement(
            """
            INSERT INTO Reportes (usuario_reporta_id, usuario_reportado_id, motivo_reporte_id, 
                                 consulta_id, fecha_reporte, comentarios)
            VALUES (?::uuid, ?::uuid, ?, ?, CURRENT_TIMESTAMP, ?)
            RETURNING id, fecha_reporte
            """
        )

        statement.setString(1, reporte.usuarioReportaId)
        statement.setString(2, reporte.usuarioReportadoId)
        statement.setInt(3, reporte.motivoReporteId)

        if (reporte.consultaId != null) {
            statement.setInt(4, reporte.consultaId)
        } else {
            statement.setNull(4, java.sql.Types.INTEGER)
        }

        statement.setString(5, reporte.comentarios)

        val resultSet = statement.executeQuery()
        val created = if (resultSet.next()) {
            reporte.copy(
                id = resultSet.getInt("id"),
                fechaReporte = resultSet.getTimestamp("fecha_reporte").toString()
            )
        } else {
            null
        }

        resultSet.close()
        statement.close()

        return created
    }

    override suspend fun delete(id: Int): Boolean {
        val statement = connection.prepareStatement(
            "DELETE FROM Reportes WHERE id = ?"
        )
        statement.setInt(1, id)

        val rowsAffected = statement.executeUpdate()
        statement.close()

        return rowsAffected > 0
    }

    private fun ResultSet.toReporte(): Reporte {
        return Reporte(
            id = getInt("id"),
            usuarioReportaId = getString("usuario_reporta_id"),
            usuarioReportadoId = getString("usuario_reportado_id"),
            motivoReporteId = getInt("motivo_reporte_id"),
            consultaId = getInt("consulta_id").let { if (wasNull()) null else it },
            fechaReporte = getTimestamp("fecha_reporte").toString(),
            comentarios = getString("comentarios"),
            usuarioReporta = Usuario(
                idUsuario = getString("usuario_reporta_id"),
                nombre = getString("reporta_nombre"),
                email = getString("reporta_email"),
                fechaRegistro = "",
                municipioId = null,
                rolId = 0,
                activo = true
            ),
            usuarioReportado = Usuario(
                idUsuario = getString("usuario_reportado_id"),
                nombre = getString("reportado_nombre"),
                email = getString("reportado_email"),
                fechaRegistro = "",
                municipioId = null,
                rolId = 0,
                activo = true
            ),
            motivoReporte = MotivoReporte(
                id = getInt("motivo_reporte_id"),
                nombre = getString("motivo_nombre"),
                descripcion = getString("motivo_descripcion")
            )
        )
    }
}