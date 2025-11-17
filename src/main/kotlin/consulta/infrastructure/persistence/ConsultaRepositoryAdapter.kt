package com.example.consulta.infrastructure.persistence

import com.example.consulta.domain.model.Consulta
import com.example.consulta.domain.port.Repository.ConsultaRepositoryPort
import com.example.shared.domain.model.Especialidad
import com.example.usuario.domain.model.Usuario
import java.sql.Connection
import java.sql.ResultSet

class ConsultaRepositoryAdapter(
    private val connection: Connection
) : ConsultaRepositoryPort {

    override suspend fun findAll(includePrivate: Boolean): List<Consulta> {
        val consultas = mutableListOf<Consulta>()
        val sql = if (includePrivate) {
            """
            SELECT c.id_consulta, c.id_usuario, c.titulo, c.pregunta, 
                   c.fecha_publicacion, c.es_privada, c.estado,
                   u.nombre as usuario_nombre, u.email as usuario_email
            FROM Consultas c
            INNER JOIN Usuarios u ON c.id_usuario = u.id_usuario
            ORDER BY c.fecha_publicacion DESC
            """
        } else {
            """
            SELECT c.id_consulta, c.id_usuario, c.titulo, c.pregunta, 
                   c.fecha_publicacion, c.es_privada, c.estado,
                   u.nombre as usuario_nombre, u.email as usuario_email
            FROM Consultas c
            INNER JOIN Usuarios u ON c.id_usuario = u.id_usuario
            WHERE c.es_privada = false
            ORDER BY c.fecha_publicacion DESC
            """
        }

        val statement = connection.prepareStatement(sql)
        val resultSet = statement.executeQuery()

        while (resultSet.next()) {
            val consulta = resultSet.toConsulta()
            consultas.add(consulta.copy(
                especialidades = getEspecialidadesByConsulta(consulta.idConsulta)
            ))
        }

        resultSet.close()
        statement.close()

        return consultas
    }

    override suspend fun findById(id: Int): Consulta? {
        val statement = connection.prepareStatement(
            """
            SELECT c.id_consulta, c.id_usuario, c.titulo, c.pregunta, 
                   c.fecha_publicacion, c.es_privada, c.estado,
                   u.nombre as usuario_nombre, u.email as usuario_email
            FROM Consultas c
            INNER JOIN Usuarios u ON c.id_usuario = u.id_usuario
            WHERE c.id_consulta = ?
            """
        )
        statement.setInt(1, id)

        val resultSet = statement.executeQuery()
        val consulta = if (resultSet.next()) {
            val c = resultSet.toConsulta()
            c.copy(
                especialidades = getEspecialidadesByConsulta(c.idConsulta)
            )
        } else {
            null
        }

        resultSet.close()
        statement.close()

        return consulta
    }

    override suspend fun findByUsuarioId(usuarioId: String): List<Consulta> {
        val consultas = mutableListOf<Consulta>()
        val statement = connection.prepareStatement(
            """
            SELECT c.id_consulta, c.id_usuario, c.titulo, c.pregunta, 
                   c.fecha_publicacion, c.es_privada, c.estado,
                   u.nombre as usuario_nombre, u.email as usuario_email
            FROM Consultas c
            INNER JOIN Usuarios u ON c.id_usuario = u.id_usuario
            WHERE c.id_usuario = ?::uuid
            ORDER BY c.fecha_publicacion DESC
            """
        )
        statement.setString(1, usuarioId)

        val resultSet = statement.executeQuery()
        while (resultSet.next()) {
            val consulta = resultSet.toConsulta()
            consultas.add(consulta.copy(
                especialidades = getEspecialidadesByConsulta(consulta.idConsulta)
            ))
        }

        resultSet.close()
        statement.close()

        return consultas
    }

    override suspend fun findByEspecialidad(especialidadId: Int): List<Consulta> {
        val consultas = mutableListOf<Consulta>()
        val statement = connection.prepareStatement(
            """
            SELECT DISTINCT c.id_consulta, c.id_usuario, c.titulo, c.pregunta, 
                   c.fecha_publicacion, c.es_privada, c.estado,
                   u.nombre as usuario_nombre, u.email as usuario_email
            FROM Consultas c
            INNER JOIN Usuarios u ON c.id_usuario = u.id_usuario
            INNER JOIN Consulta_Especialidad ce ON c.id_consulta = ce.id_consulta
            WHERE ce.id_catalogo_especialidad = ? AND c.es_privada = false
            ORDER BY c.fecha_publicacion DESC
            """
        )
        statement.setInt(1, especialidadId)

        val resultSet = statement.executeQuery()
        while (resultSet.next()) {
            val consulta = resultSet.toConsulta()
            consultas.add(consulta.copy(
                especialidades = getEspecialidadesByConsulta(consulta.idConsulta)
            ))
        }

        resultSet.close()
        statement.close()

        return consultas
    }

    override suspend fun create(consulta: Consulta, especialidadesIds: List<Int>): Consulta? {
        // Insertar consulta
        val statement = connection.prepareStatement(
            """
            INSERT INTO Consultas (id_usuario, titulo, pregunta, es_privada, estado)
            VALUES (?::uuid, ?, ?, ?, ?::estado_consulta)
            RETURNING id_consulta, fecha_publicacion
            """
        )

        statement.setString(1, consulta.idUsuario)
        statement.setString(2, consulta.titulo)
        statement.setString(3, consulta.pregunta)
        statement.setBoolean(4, consulta.esPrivada)
        statement.setString(5, consulta.estado)

        val resultSet = statement.executeQuery()
        val createdConsulta = if (resultSet.next()) {
            val idConsulta = resultSet.getInt("id_consulta")

            // Insertar especialidades
            insertEspecialidadesConsulta(idConsulta, especialidadesIds)

            consulta.copy(
                idConsulta = idConsulta,
                fechaPublicacion = resultSet.getTimestamp("fecha_publicacion").toString(),
                especialidades = getEspecialidadesByConsulta(idConsulta)
            )
        } else {
            null
        }

        resultSet.close()
        statement.close()

        return createdConsulta
    }

    override suspend fun updateEstado(id: Int, estado: String): Boolean {
        val statement = connection.prepareStatement(
            "UPDATE Consultas SET estado = ?::estado_consulta WHERE id_consulta = ?"
        )
        statement.setString(1, estado)
        statement.setInt(2, id)

        val rowsAffected = statement.executeUpdate()
        statement.close()

        return rowsAffected > 0
    }

    override suspend fun delete(id: Int): Boolean {
        val statement = connection.prepareStatement(
            "DELETE FROM Consultas WHERE id_consulta = ?"
        )
        statement.setInt(1, id)

        val rowsAffected = statement.executeUpdate()
        statement.close()

        return rowsAffected > 0
    }

    private fun insertEspecialidadesConsulta(consultaId: Int, especialidadesIds: List<Int>) {
        val statement = connection.prepareStatement(
            "INSERT INTO Consulta_Especialidad (id_consulta, id_catalogo_especialidad) VALUES (?, ?)"
        )

        for (especialidadId in especialidadesIds) {
            statement.setInt(1, consultaId)
            statement.setInt(2, especialidadId)
            statement.addBatch()
        }

        statement.executeBatch()
        statement.close()
    }

    private fun getEspecialidadesByConsulta(consultaId: Int): List<Especialidad> {
        val especialidades = mutableListOf<Especialidad>()
        val statement = connection.prepareStatement(
            """
            SELECT ce.id, ce.nombre_materia, ce.descripcion
            FROM Catalogo_Especialidad ce
            INNER JOIN Consulta_Especialidad cse ON ce.id = cse.id_catalogo_especialidad
            WHERE cse.id_consulta = ?
            """
        )
        statement.setInt(1, consultaId)

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

    private fun ResultSet.toConsulta(): Consulta {
        return Consulta(
            idConsulta = getInt("id_consulta"),
            idUsuario = getString("id_usuario"),
            titulo = getString("titulo"),
            pregunta = getString("pregunta"),
            fechaPublicacion = getTimestamp("fecha_publicacion").toString(),
            esPrivada = getBoolean("es_privada"),
            estado = getString("estado"),
            usuario = Usuario(
                idUsuario = getString("id_usuario"),
                nombre = getString("usuario_nombre"),
                email = getString("usuario_email"),
                fechaRegistro = "",
                municipioId = null,
                rolId = 0,
                activo = true
            )
        )
    }
}