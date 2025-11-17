package com.example.chat.infrastructure.persistence

import com.example.chat.domain.model.Chat
import com.example.chat.domain.port.Repository.ChatRepositoryPort
import com.example.usuario.domain.model.Abogado
import com.example.usuario.domain.model.Usuario
import java.sql.Connection
import java.sql.ResultSet

class ChatRepositoryAdapter(
    private val connection: Connection
) : ChatRepositoryPort {

    override suspend fun findAll(): List<Chat> {
        val chats = mutableListOf<Chat>()
        val statement = connection.prepareStatement(
            """
            SELECT c.id, c.usuario_cliente_id, c.usuario_abogado_id, c.fecha_inicio,
                   uc.nombre as cliente_nombre, uc.email as cliente_email,
                   ua.nombre as abogado_nombre, ua.email as abogado_email,
                   a.cedula_profesional, a.calificacion_promedio
            FROM Chats c
            INNER JOIN Usuarios uc ON c.usuario_cliente_id = uc.id_usuario
            INNER JOIN Usuarios ua ON c.usuario_abogado_id = ua.id_usuario
            INNER JOIN Abogados a ON c.usuario_abogado_id = a.id_usuario
            ORDER BY c.fecha_inicio DESC
            """
        )

        val resultSet = statement.executeQuery()
        while (resultSet.next()) {
            chats.add(resultSet.toChat())
        }

        resultSet.close()
        statement.close()

        return chats
    }

    override suspend fun findById(id: Int): Chat? {
        val statement = connection.prepareStatement(
            """
            SELECT c.id, c.usuario_cliente_id, c.usuario_abogado_id, c.fecha_inicio,
                   uc.nombre as cliente_nombre, uc.email as cliente_email,
                   ua.nombre as abogado_nombre, ua.email as abogado_email,
                   a.cedula_profesional, a.calificacion_promedio
            FROM Chats c
            INNER JOIN Usuarios uc ON c.usuario_cliente_id = uc.id_usuario
            INNER JOIN Usuarios ua ON c.usuario_abogado_id = ua.id_usuario
            INNER JOIN Abogados a ON c.usuario_abogado_id = a.id_usuario
            WHERE c.id = ?
            """
        )
        statement.setInt(1, id)

        val resultSet = statement.executeQuery()
        val chat = if (resultSet.next()) {
            resultSet.toChat()
        } else {
            null
        }

        resultSet.close()
        statement.close()

        return chat
    }

    override suspend fun findByUsuarioId(usuarioId: String): List<Chat> {
        val chats = mutableListOf<Chat>()
        val statement = connection.prepareStatement(
            """
            SELECT c.id, c.usuario_cliente_id, c.usuario_abogado_id, c.fecha_inicio,
                   uc.nombre as cliente_nombre, uc.email as cliente_email,
                   ua.nombre as abogado_nombre, ua.email as abogado_email,
                   a.cedula_profesional, a.calificacion_promedio
            FROM Chats c
            INNER JOIN Usuarios uc ON c.usuario_cliente_id = uc.id_usuario
            INNER JOIN Usuarios ua ON c.usuario_abogado_id = ua.id_usuario
            INNER JOIN Abogados a ON c.usuario_abogado_id = a.id_usuario
            WHERE c.usuario_cliente_id = ?::uuid OR c.usuario_abogado_id = ?::uuid
            ORDER BY c.fecha_inicio DESC
            """
        )
        statement.setString(1, usuarioId)
        statement.setString(2, usuarioId)

        val resultSet = statement.executeQuery()
        while (resultSet.next()) {
            chats.add(resultSet.toChat())
        }

        resultSet.close()
        statement.close()

        return chats
    }

    override suspend fun findByParticipantes(clienteId: String, abogadoId: String): Chat? {
        val statement = connection.prepareStatement(
            """
            SELECT c.id, c.usuario_cliente_id, c.usuario_abogado_id, c.fecha_inicio,
                   uc.nombre as cliente_nombre, uc.email as cliente_email,
                   ua.nombre as abogado_nombre, ua.email as abogado_email,
                   a.cedula_profesional, a.calificacion_promedio
            FROM Chats c
            INNER JOIN Usuarios uc ON c.usuario_cliente_id = uc.id_usuario
            INNER JOIN Usuarios ua ON c.usuario_abogado_id = ua.id_usuario
            INNER JOIN Abogados a ON c.usuario_abogado_id = a.id_usuario
            WHERE c.usuario_cliente_id = ?::uuid AND c.usuario_abogado_id = ?::uuid
            """
        )
        statement.setString(1, clienteId)
        statement.setString(2, abogadoId)

        val resultSet = statement.executeQuery()
        val chat = if (resultSet.next()) {
            resultSet.toChat()
        } else {
            null
        }

        resultSet.close()
        statement.close()

        return chat
    }

    override suspend fun create(chat: Chat): Chat? {
        val statement = connection.prepareStatement(
            """
            INSERT INTO Chats (usuario_cliente_id, usuario_abogado_id, fecha_inicio)
            VALUES (?::uuid, ?::uuid, CURRENT_TIMESTAMP)
            RETURNING id, fecha_inicio
            """
        )

        statement.setString(1, chat.usuarioClienteId)
        statement.setString(2, chat.usuarioAbogadoId)

        val resultSet = statement.executeQuery()
        val created = if (resultSet.next()) {
            chat.copy(
                id = resultSet.getInt("id"),
                fechaInicio = resultSet.getTimestamp("fecha_inicio").toString()
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
            "DELETE FROM Chats WHERE id = ?"
        )
        statement.setInt(1, id)

        val rowsAffected = statement.executeUpdate()
        statement.close()

        return rowsAffected > 0
    }

    private fun ResultSet.toChat(): Chat {
        return Chat(
            id = getInt("id"),
            usuarioClienteId = getString("usuario_cliente_id"),
            usuarioAbogadoId = getString("usuario_abogado_id"),
            fechaInicio = getTimestamp("fecha_inicio").toString(),
            cliente = Usuario(
                idUsuario = getString("usuario_cliente_id"),
                nombre = getString("cliente_nombre"),
                email = getString("cliente_email"),
                fechaRegistro = "",
                municipioId = null,
                rolId = 1,
                activo = true
            ),
            abogado = Abogado(
                idUsuario = getString("usuario_abogado_id"),
                cedulaProfesional = getString("cedula_profesional"),
                biografia = null,
                calificacionPromedio = getDouble("calificacion_promedio"),
                usuario = Usuario(
                    idUsuario = getString("usuario_abogado_id"),
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