package com.example.chat.infrastructure.persistence

import com.example.chat.domain.model.Mensaje
import com.example.chat.domain.port.MensajeRepositoryPort
import com.example.usuario.domain.model.Usuario
import java.sql.Connection
import java.sql.ResultSet

class MensajeRepositoryAdapter(
    private val connection: Connection
) : MensajeRepositoryPort {

    override suspend fun findByChatId(chatId: Int): List<Mensaje> {
        val mensajes = mutableListOf<Mensaje>()
        val statement = connection.prepareStatement(
            """
            SELECT m.id, m.chat_id, m.remitente_id, m.mensaje, m.fecha,
                   u.nombre as remitente_nombre, u.email as remitente_email
            FROM Mensajes m
            INNER JOIN Usuarios u ON m.remitente_id = u.id_usuario
            WHERE m.chat_id = ?
            ORDER BY m.fecha ASC
            """
        )
        statement.setInt(1, chatId)

        val resultSet = statement.executeQuery()
        while (resultSet.next()) {
            mensajes.add(resultSet.toMensaje())
        }

        resultSet.close()
        statement.close()

        return mensajes
    }

    override suspend fun findById(id: Int): Mensaje? {
        val statement = connection.prepareStatement(
            """
            SELECT m.id, m.chat_id, m.remitente_id, m.mensaje, m.fecha,
                   u.nombre as remitente_nombre, u.email as remitente_email
            FROM Mensajes m
            INNER JOIN Usuarios u ON m.remitente_id = u.id_usuario
            WHERE m.id = ?
            """
        )
        statement.setInt(1, id)

        val resultSet = statement.executeQuery()
        val mensaje = if (resultSet.next()) {
            resultSet.toMensaje()
        } else {
            null
        }

        resultSet.close()
        statement.close()

        return mensaje
    }

    override suspend fun create(mensaje: Mensaje): Mensaje? {
        val statement = connection.prepareStatement(
            """
            INSERT INTO Mensajes (chat_id, remitente_id, mensaje, fecha)
            VALUES (?, ?::uuid, ?, CURRENT_TIMESTAMP)
            RETURNING id, fecha
            """
        )

        statement.setInt(1, mensaje.chatId)
        statement.setString(2, mensaje.remitenteId)
        statement.setString(3, mensaje.mensaje)

        val resultSet = statement.executeQuery()
        val created = if (resultSet.next()) {
            mensaje.copy(
                id = resultSet.getInt("id"),
                fecha = resultSet.getTimestamp("fecha").toString()
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
            "DELETE FROM Mensajes WHERE id = ?"
        )
        statement.setInt(1, id)

        val rowsAffected = statement.executeUpdate()
        statement.close()

        return rowsAffected > 0
    }

    private fun ResultSet.toMensaje(): Mensaje {
        return Mensaje(
            id = getInt("id"),
            chatId = getInt("chat_id"),
            remitenteId = getString("remitente_id"),
            mensaje = getString("mensaje"),
            fecha = getTimestamp("fecha").toString(),
            remitente = Usuario(
                idUsuario = getString("remitente_id"),
                nombre = getString("remitente_nombre"),
                email = getString("remitente_email"),
                fechaRegistro = "",
                municipioId = null,
                rolId = 0,
                activo = true
            )
        )
    }
}