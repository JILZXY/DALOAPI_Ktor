package com.example.calificacion.infrastructure.persistence

import com.example.calificacion.domain.model.Calificacion
import com.example.calificacion.domain.model.CalificacionPromedio
import com.example.calificacion.domain.port.Repository.CalificacionRepositoryPort
import java.sql.Connection
import java.sql.ResultSet

class CalificacionRepositoryAdapter(
    private val connection: Connection
) : CalificacionRepositoryPort {

    override suspend fun findByAbogadoId(abogadoId: String): List<Calificacion> {
        val calificaciones = mutableListOf<Calificacion>()
        val statement = connection.prepareStatement(
            """
            SELECT c.id_calificacion, c.id_usuario, c.id_abogado,
                   c.atencion, c.profesionalismo, c.claridad, c.empatia,
                   c.comentario_opcional, c.fecha
            FROM Calificaciones c
            WHERE c.id_abogado = ?::uuid
            ORDER BY c.fecha DESC
            """
        )
        statement.setString(1, abogadoId)

        val resultSet = statement.executeQuery()
        while (resultSet.next()) {
            calificaciones.add(resultSet.toCalificacion())
        }

        resultSet.close()
        statement.close()

        return calificaciones
    }

    override suspend fun findById(id: Int): Calificacion? {
        val statement = connection.prepareStatement(
            """
            SELECT c.id_calificacion, c.id_usuario, c.id_abogado,
                   c.atencion, c.profesionalismo, c.claridad, c.empatia,
                   c.comentario_opcional, c.fecha
            FROM Calificaciones c
            WHERE c.id_calificacion = ?
            """
        )
        statement.setInt(1, id)

        val resultSet = statement.executeQuery()
        val calificacion = if (resultSet.next()) {
            resultSet.toCalificacion()
        } else {
            null
        }

        resultSet.close()
        statement.close()

        return calificacion
    }

    override suspend fun create(calificacion: Calificacion): Calificacion? {
        val statement = connection.prepareStatement(
            """
            INSERT INTO Calificaciones (id_usuario, id_abogado, atencion, profesionalismo, 
                                       claridad, empatia, comentario_opcional, fecha)
            VALUES (?::uuid, ?::uuid, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
            RETURNING id_calificacion, fecha
            """
        )

        statement.setString(1, calificacion.idUsuario)
        statement.setString(2, calificacion.idAbogado)
        statement.setInt(3, calificacion.atencion)
        statement.setInt(4, calificacion.profesionalismo)
        statement.setInt(5, calificacion.claridad)
        statement.setInt(6, calificacion.empatia)
        statement.setString(7, calificacion.comentarioOpcional)

        val resultSet = statement.executeQuery()
        val created = if (resultSet.next()) {
            calificacion.copy(
                idCalificacion = resultSet.getInt("id_calificacion"),
                fecha = resultSet.getTimestamp("fecha").toString()
            )
        } else {
            null
        }

        resultSet.close()
        statement.close()

        return created
    }

    override suspend fun getPromediosByAbogado(abogadoId: String): CalificacionPromedio {
        val statement = connection.prepareStatement(
            """
            SELECT 
                AVG(atencion) as atencion_promedio,
                AVG(profesionalismo) as profesionalismo_promedio,
                AVG(claridad) as claridad_promedio,
                AVG(empatia) as empatia_promedio
            FROM Calificaciones
            WHERE id_abogado = ?::uuid
            """
        )
        statement.setString(1, abogadoId)

        val resultSet = statement.executeQuery()
        val promedio = if (resultSet.next()) {
            val atencionProm = resultSet.getDouble("atencion_promedio").let {
                if (resultSet.wasNull()) 0.0 else it
            }
            val profesionalismoProm = resultSet.getDouble("profesionalismo_promedio").let {
                if (resultSet.wasNull()) 0.0 else it
            }
            val claridadProm = resultSet.getDouble("claridad_promedio").let {
                if (resultSet.wasNull()) 0.0 else it
            }
            val empatiaProm = resultSet.getDouble("empatia_promedio").let {
                if (resultSet.wasNull()) 0.0 else it
            }

            // Calcular promedio general (promedio de los promedios)
            val promedioGeneral = if (atencionProm > 0 || profesionalismoProm > 0 ||
                claridadProm > 0 || empatiaProm > 0) {
                (atencionProm + profesionalismoProm + claridadProm + empatiaProm) / 4.0
            } else {
                0.0
            }

            CalificacionPromedio(
                atencionPromedio = atencionProm,
                profesionalismoPromedio = profesionalismoProm,
                claridadPromedio = claridadProm,
                empatiaPromedio = empatiaProm,
                promedioGeneral = promedioGeneral
            )
        } else {
            CalificacionPromedio(0.0, 0.0, 0.0, 0.0, 0.0)
        }

        resultSet.close()
        statement.close()

        return promedio
    }

    override suspend fun updateAbogadoCalificacionPromedio(abogadoId: String): Boolean {
        // Obtener el promedio general
        val promedio = getPromediosByAbogado(abogadoId)

        // Actualizar en la tabla Abogados
        val statement = connection.prepareStatement(
            """
            UPDATE Abogados 
            SET calificacion_promedio = ?
            WHERE id_usuario = ?::uuid
            """
        )
        statement.setDouble(1, promedio.promedioGeneral)
        statement.setString(2, abogadoId)

        val rowsAffected = statement.executeUpdate()
        statement.close()

        return rowsAffected > 0
    }

    private fun ResultSet.toCalificacion(): Calificacion {
        return Calificacion(
            idCalificacion = getInt("id_calificacion"),
            idUsuario = getString("id_usuario"),
            idAbogado = getString("id_abogado"),
            atencion = getInt("atencion"),
            profesionalismo = getInt("profesionalismo"),
            claridad = getInt("claridad"),
            empatia = getInt("empatia"),
            comentarioOpcional = getString("comentario_opcional"),
            fecha = getTimestamp("fecha").toString()
        )
    }
}