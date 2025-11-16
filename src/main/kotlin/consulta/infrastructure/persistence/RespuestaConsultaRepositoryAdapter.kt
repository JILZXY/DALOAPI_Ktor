package com.example.consulta.infrastructure.persistence

import com.example.consulta.domain.model.RespuestaConsulta
import com.example.consulta.domain.port.RespuestaConsultaRepositoryPort
import com.example.usuario.domain.model.Abogado
import com.example.usuario.domain.model.Usuario
import java.sql.Connection
import java.sql.ResultSet

class RespuestaConsultaRepositoryAdapter(
    private val connection: Connection
) : RespuestaConsultaRepositoryPort {

    override suspend fun findByConsultaId(consultaId: Int): List<RespuestaConsulta> {
        val respuestas = mutableListOf<RespuestaConsulta>()
        val statement = connection.prepareStatement(
            """
            SELECT rc.id_respuesta, rc.id_consulta, rc.id_abogado, rc.respuesta,
                   rc.fecha_respuesta, rc.likes,
                   u.nombre as abogado_nombre, u.email as abogado_email,
                   a.cedula_profesional, a.biografia, a.calificacion_promedio
            FROM Respuesta_Consulta rc
            INNER JOIN Abogados a ON rc.id_abogado = a.id_usuario
            INNER JOIN Usuarios u ON a.id_usuario = u.id_usuario
            WHERE rc.id_consulta = ?
            ORDER BY rc.fecha_respuesta DESC
            """
        )
        statement.setInt(1, consultaId)

        val resultSet = statement.executeQuery()
        while (resultSet.next()) {
            respuestas.add(resultSet.toRespuestaConsulta())
        }

        resultSet.close()
        statement.close()

        return respuestas
    }

    override suspend fun findById(id: Int): RespuestaConsulta? {
        val statement = connection.prepareStatement(
            """
            SELECT rc.id_respuesta, rc.id_consulta, rc.id_abogado, rc.respuesta,
                   rc.fecha_respuesta, rc.likes,
                   u.nombre as abogado_nombre, u.email as abogado_email,
                   a.cedula_profesional, a.biografia, a.calificacion_promedio
            FROM Respuesta_Consulta rc
            INNER JOIN Abogados a ON rc.id_abogado = a.id_usuario
            INNER JOIN Usuarios u ON a.id_usuario = u.id_usuario
            WHERE rc.id_respuesta = ?
            """
        )
        statement.setInt(1, id)

        val resultSet = statement.executeQuery()
        val respuesta = if (resultSet.next()) {
            resultSet.toRespuestaConsulta()
        } else {
            null
        }

        resultSet.close()
        statement.close()

        return respuesta
    }

    override suspend fun create(respuesta: RespuestaConsulta): RespuestaConsulta? {
        val statement = connection.prepareStatement(
            """
            INSERT INTO Respuesta_Consulta (id_consulta, id_abogado, respuesta)
            VALUES (?, ?::uuid, ?)
            RETURNING id_respuesta, fecha_respuesta, likes
            """
        )

        statement.setInt(1, respuesta.idConsulta)
        statement.setString(2, respuesta.idAbogado)
        statement.setString(3, respuesta.respuesta)

        val resultSet = statement.executeQuery()
        val createdRespuesta = if (resultSet.next()) {
            respuesta.copy(
                idRespuesta = resultSet.getInt("id_respuesta"),
                fechaRespuesta = resultSet.getTimestamp("fecha_respuesta").toString(),
                likes = resultSet.getInt("likes")
            )
        } else {
            null
        }

        resultSet.close()
        statement.close()

        return createdRespuesta
    }

    override suspend fun addLike(id: Int): Boolean {
        val statement = connection.prepareStatement(
            "UPDATE Respuesta_Consulta SET likes = likes + 1 WHERE id_respuesta = ?"
        )
        statement.setInt(1, id)

        val rowsAffected = statement.executeUpdate()
        statement.close()

        return rowsAffected > 0
    }

    override suspend fun delete(id: Int): Boolean {
        val statement = connection.prepareStatement(
            "DELETE FROM Respuesta_Consulta WHERE id_respuesta = ?"
        )
        statement.setInt(1, id)

        val rowsAffected = statement.executeUpdate()
        statement.close()

        return rowsAffected > 0
    }

    private fun ResultSet.toRespuestaConsulta(): RespuestaConsulta {
        return RespuestaConsulta(
            idRespuesta = getInt("id_respuesta"),
            idConsulta = getInt("id_consulta"),
            idAbogado = getString("id_abogado"),
            respuesta = getString("respuesta"),
            fechaRespuesta = getTimestamp("fecha_respuesta").toString(),
            likes = getInt("likes"),
            abogado = Abogado(
                idUsuario = getString("id_abogado"),
                cedulaProfesional = getString("cedula_profesional"),
                biografia = getString("biografia"),
                calificacionPromedio = getDouble("calificacion_promedio"),
                usuario = Usuario(
                    idUsuario = getString("id_abogado"),
                    nombre = getString("abogado_nombre"),
                    email = getString("abogado_email"),
                    fechaRegistro = "",
                    municipioId = null,
                    rolId = 2,
                    activo = true
                )
            )
        )
    }
}