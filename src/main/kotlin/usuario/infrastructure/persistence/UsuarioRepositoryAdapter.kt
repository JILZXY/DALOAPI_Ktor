package com.example.usuario.infrastructure.persistence

import com.example.municipio.domain.model.Municipio
import com.example.estado.domain.model.Estado
import com.example.shared.domain.model.Rol
import com.example.usuario.domain.model.Abogado
import com.example.usuario.domain.model.Usuario
import com.example.usuario.domain.port.Repository.UsuarioRepositoryPort
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException

class UsuarioRepositoryAdapter(
    private val connection: Connection
) : UsuarioRepositoryPort {

    override suspend fun findAll(): List<Usuario> {
        val usuarios = mutableListOf<Usuario>()
        val statement = connection.prepareStatement(
            """
            SELECT 
                u.id_usuario, u.nombre, u.email, u.fecha_registro,
                u.municipio_id, u.rol_id, u.activo,
                r.nombre as rol_nombre,
                m.nombre as municipio_nombre,
                e.id as estado_id, e.nombre as estado_nombre
            FROM Usuarios u
            LEFT JOIN Roles r ON u.rol_id = r.id
            LEFT JOIN Municipios m ON u.municipio_id = m.id
            LEFT JOIN Estados e ON m.estado_id = e.id
            ORDER BY u.fecha_registro DESC
            """
        )

        val resultSet = statement.executeQuery()
        while (resultSet.next()) {
            usuarios.add(resultSet.toUsuario())
        }

        resultSet.close()
        statement.close()

        return usuarios
    }

    override suspend fun findById(id: String): Usuario? {
        val statement = connection.prepareStatement(
            """
            SELECT 
                u.id_usuario, u.nombre, u.email, u.fecha_registro,
                u.municipio_id, u.rol_id, u.activo,
                r.nombre as rol_nombre,
                m.nombre as municipio_nombre,
                e.id as estado_id, e.nombre as estado_nombre
            FROM Usuarios u
            LEFT JOIN Roles r ON u.rol_id = r.id
            LEFT JOIN Municipios m ON u.municipio_id = m.id
            LEFT JOIN Estados e ON m.estado_id = e.id
            WHERE u.id_usuario = ?::uuid
            """
        )
        statement.setString(1, id)

        val resultSet = statement.executeQuery()
        val usuario = if (resultSet.next()) {
            resultSet.toUsuario()
        } else {
            null
        }

        resultSet.close()
        statement.close()

        return usuario
    }

    override suspend fun findByEmail(email: String): Usuario? {
        val statement = connection.prepareStatement(
            """
            SELECT 
                u.id_usuario, u.nombre, u.email, u.fecha_registro,
                u.municipio_id, u.rol_id, u.activo,
                r.nombre as rol_nombre,
                m.nombre as municipio_nombre,
                e.id as estado_id, e.nombre as estado_nombre
            FROM Usuarios u
            LEFT JOIN Roles r ON u.rol_id = r.id
            LEFT JOIN Municipios m ON u.municipio_id = m.id
            LEFT JOIN Estados e ON m.estado_id = e.id
            WHERE u.email = ?
            """
        )
        statement.setString(1, email)

        val resultSet = statement.executeQuery()
        val usuario = if (resultSet.next()) {
            resultSet.toUsuario()
        } else {
            null
        }

        resultSet.close()
        statement.close()

        return usuario
    }

    override suspend fun create(usuario: Usuario, passwordHash: String): Usuario? {
        val statement = connection.prepareStatement(
            """
            INSERT INTO Usuarios (nombre, email, contrasena, municipio_id, rol_id, activo)
            VALUES (?, ?, ?, ?, ?, ?)
            RETURNING id_usuario, fecha_registro
            """
        )

        statement.setString(1, usuario.nombre)
        statement.setString(2, usuario.email)
        statement.setString(3, passwordHash)
        if (usuario.municipioId != null) {
            statement.setInt(4, usuario.municipioId)
        } else {
            statement.setNull(4, java.sql.Types.INTEGER)
        }
        statement.setInt(5, usuario.rolId)
        statement.setBoolean(6, usuario.activo)

        val resultSet = statement.executeQuery()
        val createdUsuario = if (resultSet.next()) {
            usuario.copy(
                idUsuario = resultSet.getString("id_usuario"),
                fechaRegistro = resultSet.getTimestamp("fecha_registro").toString()
            )
        } else {
            null
        }

        resultSet.close()
        statement.close()

        return createdUsuario
    }

    override suspend fun saveAbogadoCompleto(
        usuario: Usuario,
        abogado: Abogado,
        passwordHash: String,
        especialidadesIds: List<Int>
    ): Usuario? {
        try {
            connection.autoCommit = false // Iniciar transacción

            // 1. Insertar Usuario
            val stmtUsuario = connection.prepareStatement(
                """
            INSERT INTO Usuarios (nombre, email, contrasena, municipio_id, rol_id, activo)
            VALUES (?, ?, ?, ?, ?, ?)
            RETURNING id_usuario, fecha_registro
            """
            )
            stmtUsuario.setString(1, usuario.nombre)
            stmtUsuario.setString(2, usuario.email)
            stmtUsuario.setString(3, passwordHash) // Necesitas pasar el hash
            usuario.municipioId?.let { stmtUsuario.setInt(4, it) } ?: stmtUsuario.setNull(4, java.sql.Types.INTEGER)
            stmtUsuario.setInt(5, 2) // rol abogado
            stmtUsuario.setBoolean(6, false) // inactivo

            val rsUsuario = stmtUsuario.executeQuery()
            if (!rsUsuario.next()) {
                connection.rollback()
                return null
            }

            val usuarioId = rsUsuario.getString("id_usuario")

            // 2. Insertar Abogado
            val stmtAbogado = connection.prepareStatement(
                """
            INSERT INTO Abogados (id_usuario, cedula_profesional, biografia, calificacion_promedio)
            VALUES (?::uuid, ?, ?, 0.00)
            """
            )
            stmtAbogado.setString(1, usuarioId)
            stmtAbogado.setString(2, abogado.cedulaProfesional)
            stmtAbogado.setString(3, abogado.biografia)
            stmtAbogado.executeUpdate()

            // 3. Insertar Especialidades
            val stmtEsp = connection.prepareStatement(
                """
            INSERT INTO Abogado_Especialidad (id_abogado, id_catalogo_especialidad)
            VALUES (?::uuid, ?)
            """
            )
            for (espId in especialidadesIds) {
                stmtEsp.setString(1, usuarioId)
                stmtEsp.setInt(2, espId)
                stmtEsp.addBatch()
            }
            stmtEsp.executeBatch()

            connection.commit() // Confirmar transacción
            connection.autoCommit = true

            return usuario.copy(idUsuario = usuarioId)

        } catch (e: Exception) {
            connection.rollback()
            connection.autoCommit = true
            return null
        }
    }

    override suspend fun update(usuario: Usuario): Usuario? {
        val statement = connection.prepareStatement(
            """
            UPDATE Usuarios 
            SET nombre = ?, municipio_id = ?, activo = ?
            WHERE id_usuario = ?::uuid
            RETURNING fecha_registro
            """
        )

        statement.setString(1, usuario.nombre)
        if (usuario.municipioId != null) {
            statement.setInt(2, usuario.municipioId)
        } else {
            statement.setNull(2, java.sql.Types.INTEGER)
        }
        statement.setBoolean(3, usuario.activo)
        statement.setString(4, usuario.idUsuario)

        val resultSet = statement.executeQuery()
        val updated = if (resultSet.next()) {
            usuario
        } else {
            null
        }

        resultSet.close()
        statement.close()

        return updated
    }

    override suspend fun delete(id: String): Boolean {
        val statement = connection.prepareStatement(
            "UPDATE Usuarios SET activo = false WHERE id_usuario = ?::uuid"
        )
        statement.setString(1, id)

        val rowsAffected = statement.executeUpdate()
        statement.close()

        return rowsAffected > 0
    }

    private fun ResultSet.toUsuario(): Usuario {
        val municipioId = getInt("municipio_id")
        val estadoId = getInt("estado_id")

        return Usuario(
            idUsuario = getString("id_usuario"),
            nombre = getString("nombre"),
            email = getString("email"),
            fechaRegistro = getTimestamp("fecha_registro").toString(),
            municipioId = if (wasNull()) null else municipioId,
            rolId = getInt("rol_id"),
            activo = getBoolean("activo"),
            municipio = if (municipioId != 0) Municipio(
                id = municipioId,
                nombre = getString("municipio_nombre") ?: "",
                estadoId = estadoId,
                estado = if (estadoId != 0) Estado(
                    id = estadoId,
                    nombre = getString("estado_nombre") ?: ""
                ) else null
            ) else null,
            rol = Rol(
                id = getInt("rol_id"),
                nombre = getString("rol_nombre") ?: ""
            )
        )
    }

    override suspend fun findInactivos(): List<Usuario> {
        val usuarios = mutableListOf<Usuario>()
        val statement = connection.prepareStatement(
            """
            SELECT 
                u.id_usuario, u.nombre, u.email, u.fecha_registro,
                u.municipio_id, u.rol_id, u.activo,
                r.nombre as rol_nombre,
                m.nombre as municipio_nombre,
                e.id as estado_id, e.nombre as estado_nombre
            FROM Usuarios u
            LEFT JOIN Roles r ON u.rol_id = r.id
            LEFT JOIN Municipios m ON u.municipio_id = m.id
            LEFT JOIN Estados e ON m.estado_id = e.id
            WHERE u.activo = false
            ORDER BY u.fecha_registro DESC
            """
        )

        val resultSet = statement.executeQuery()
        while (resultSet.next()) {
            usuarios.add(resultSet.toUsuario())
        }

        resultSet.close()
        statement.close()

        return usuarios
    }

    override suspend fun activar(id: String): Boolean {
        val statement = connection.prepareStatement(
            "UPDATE Usuarios SET activo = true WHERE id_usuario = ?::uuid"
        )
        statement.setString(1, id)

        val rowsAffected = statement.executeUpdate()
        statement.close()

        return rowsAffected > 0
    }
}