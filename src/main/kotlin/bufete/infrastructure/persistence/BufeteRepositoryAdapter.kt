package com.example.bufete.infrastructure.persistence

import com.example.bufete.domain.model.Bufete
import com.example.bufete.domain.port.Repository.BufeteRepositoryPort
import com.example.shared.domain.model.Especialidad
import java.sql.Connection
import java.sql.ResultSet

class BufeteRepositoryAdapter(
    private val connection: Connection
) : BufeteRepositoryPort {

    override suspend fun findAll(): List<Bufete> {
        val bufetes = mutableListOf<Bufete>()
        val statement = connection.prepareStatement(
            """
            SELECT b.id, b.admin_bufete_id, b.nombre, b.descripcion, 
                   b.logo, b.fecha_creacion
            FROM Bufetes b
            ORDER BY b.nombre
            """
        )

        val resultSet = statement.executeQuery()
        while (resultSet.next()) {
            val bufete = resultSet.toBufete()
            bufetes.add(bufete.copy(
                especialidades = getEspecialidadesByBufete(bufete.id),
                calificacionPromedio = calculateCalificacionPromedio(bufete.id)
            ))
        }

        resultSet.close()
        statement.close()

        return bufetes
    }

    override suspend fun findById(id: Int): Bufete? {
        val statement = connection.prepareStatement(
            """
            SELECT b.id, b.admin_bufete_id, b.nombre, b.descripcion, 
                   b.logo, b.fecha_creacion
            FROM Bufetes b
            WHERE b.id = ?
            """
        )
        statement.setInt(1, id)

        val resultSet = statement.executeQuery()
        val bufete = if (resultSet.next()) {
            val buf = resultSet.toBufete()
            buf.copy(
                especialidades = getEspecialidadesByBufete(buf.id),
                calificacionPromedio = calculateCalificacionPromedio(buf.id)
            )
        } else {
            null
        }

        resultSet.close()
        statement.close()

        return bufete
    }

    override suspend fun findByAdminId(adminId: String): List<Bufete> {
        val bufetes = mutableListOf<Bufete>()
        val statement = connection.prepareStatement(
            """
            SELECT b.id, b.admin_bufete_id, b.nombre, b.descripcion, 
                   b.logo, b.fecha_creacion
            FROM Bufetes b
            WHERE b.admin_bufete_id = ?::uuid
            ORDER BY b.fecha_creacion DESC
            """
        )
        statement.setString(1, adminId)

        val resultSet = statement.executeQuery()
        while (resultSet.next()) {
            val bufete = resultSet.toBufete()
            bufetes.add(bufete.copy(
                especialidades = getEspecialidadesByBufete(bufete.id),
                calificacionPromedio = calculateCalificacionPromedio(bufete.id)
            ))
        }

        resultSet.close()
        statement.close()

        return bufetes
    }

    override suspend fun create(bufete: Bufete, especialidadesIds: List<Int>): Bufete? {
        val statement = connection.prepareStatement(
            """
            INSERT INTO Bufetes (admin_bufete_id, nombre, descripcion, logo, fecha_creacion)
            VALUES (?::uuid, ?, ?, ?, CURRENT_TIMESTAMP)
            RETURNING id, fecha_creacion
            """
        )

        statement.setString(1, bufete.adminBufeteId)
        statement.setString(2, bufete.nombre)
        statement.setString(3, bufete.descripcion)
        statement.setString(4, bufete.logo)

        val resultSet = statement.executeQuery()
        val createdBufete = if (resultSet.next()) {
            val bufeteId = resultSet.getInt("id")

            // Insertar especialidades
            if (especialidadesIds.isNotEmpty()) {
                insertEspecialidadesBufete(bufeteId, especialidadesIds)
            }

            bufete.copy(
                id = bufeteId,
                fechaCreacion = resultSet.getTimestamp("fecha_creacion").toString(),
                especialidades = getEspecialidadesByBufete(bufeteId)
            )
        } else {
            null
        }

        resultSet.close()
        statement.close()

        return createdBufete
    }

    override suspend fun update(bufete: Bufete): Bufete? {
        val statement = connection.prepareStatement(
            """
            UPDATE Bufetes 
            SET nombre = ?, descripcion = ?, logo = ?
            WHERE id = ?
            """
        )

        statement.setString(1, bufete.nombre)
        statement.setString(2, bufete.descripcion)
        statement.setString(3, bufete.logo)
        statement.setInt(4, bufete.id)

        val rowsAffected = statement.executeUpdate()
        statement.close()

        return if (rowsAffected > 0) bufete else null
    }

    override suspend fun delete(id: Int): Boolean {
        val statement = connection.prepareStatement(
            "DELETE FROM Bufetes WHERE id = ?"
        )
        statement.setInt(1, id)

        val rowsAffected = statement.executeUpdate()
        statement.close()

        return rowsAffected > 0
    }

    override suspend fun calculateCalificacionPromedio(bufeteId: Int): Double {
        // Obtener promedio de calificaciones de todos los abogados del bufete
        val statement = connection.prepareStatement(
            """
            SELECT AVG(a.calificacion_promedio) as promedio
            FROM Abogados a
            INNER JOIN Solicitudes_Bufete sb ON a.id_usuario = sb.abogado_id
            WHERE sb.bufete_id = ? AND sb.estado = 'Aprobado'
            """
        )
        statement.setInt(1, bufeteId)

        val resultSet = statement.executeQuery()
        val promedio = if (resultSet.next()) {
            resultSet.getDouble("promedio").let {
                if (resultSet.wasNull()) 0.0 else it
            }
        } else {
            0.0
        }

        resultSet.close()
        statement.close()

        return promedio
    }

    private fun insertEspecialidadesBufete(bufeteId: Int, especialidadesIds: List<Int>) {
        val statement = connection.prepareStatement(
            "INSERT INTO Bufete_Materia (bufete_id, id_catalogo_especialidad) VALUES (?, ?) ON CONFLICT DO NOTHING"
        )

        for (especialidadId in especialidadesIds) {
            statement.setInt(1, bufeteId)
            statement.setInt(2, especialidadId)
            statement.addBatch()
        }

        statement.executeBatch()
        statement.close()
    }

    private fun getEspecialidadesByBufete(bufeteId: Int): List<Especialidad> {
        val especialidades = mutableListOf<Especialidad>()
        val statement = connection.prepareStatement(
            """
            SELECT ce.id, ce.nombre_materia, ce.descripcion
            FROM Catalogo_Especialidad ce
            INNER JOIN Bufete_Materia bm ON ce.id = bm.id_catalogo_especialidad
            WHERE bm.bufete_id = ?
            """
        )
        statement.setInt(1, bufeteId)

        val resultSet = statement.executeQuery()
        while (resultSet.next()) {
            especialidades.add(
                Especialidad(
                    id = resultSet.getInt("id"),
                    nombreMateria = resultSet.getString("nombre_materia"),
                    descripcion = resultSet.getString("descripcion")
                )
            )
        }

        resultSet.close()
        statement.close()

        return especialidades
    }

    private fun ResultSet.toBufete(): Bufete {
        return Bufete(
            id = getInt("id"),
            adminBufeteId = getString("admin_bufete_id"),
            nombre = getString("nombre"),
            descripcion = getString("descripcion"),
            logo = getString("logo"),
            fechaCreacion = getTimestamp("fecha_creacion").toString()
        )
    }
}