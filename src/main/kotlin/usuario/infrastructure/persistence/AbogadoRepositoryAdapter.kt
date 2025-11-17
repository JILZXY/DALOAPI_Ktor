package com.example.usuario.infrastructure.persistence

import com.example.shared.domain.model.Especialidad
import com.example.usuario.domain.port.Repository.AbogadoRepositoryPort
import com.example.usuario.domain.model.*
import java.sql.Connection
import java.sql.ResultSet

class AbogadoRepositoryAdapter(
    private val connection: Connection
) : AbogadoRepositoryPort {

    override suspend fun findAll(): List<Abogado> {
        val abogados = mutableListOf<Abogado>()
        val statement = connection.prepareStatement(
            """
            SELECT 
                a.id_usuario, a.cedula_profesional, a.biografia, a.calificacion_promedio,
                u.nombre, u.email, u.fecha_registro, u.municipio_id, u.rol_id, u.activo
            FROM Abogados a
            INNER JOIN Usuarios u ON a.id_usuario = u.id_usuario
            WHERE u.activo = true
            ORDER BY a.calificacion_promedio DESC
            """
        )

        val resultSet = statement.executeQuery()
        while (resultSet.next()) {
            val abogado = resultSet.toAbogado()
            abogados.add(abogado.copy(
                especialidades = getEspecialidadesByAbogado(abogado.idUsuario)
            ))
        }

        resultSet.close()
        statement.close()

        return abogados
    }

    override suspend fun findById(id: String): Abogado? {
        val statement = connection.prepareStatement(
            """
            SELECT 
                a.id_usuario, a.cedula_profesional, a.biografia, a.calificacion_promedio,
                u.nombre, u.email, u.fecha_registro, u.municipio_id, u.rol_id, u.activo
            FROM Abogados a
            INNER JOIN Usuarios u ON a.id_usuario = u.id_usuario
            WHERE a.id_usuario = ?::uuid
            """
        )
        statement.setString(1, id)

        val resultSet = statement.executeQuery()
        val abogado = if (resultSet.next()) {
            val abo = resultSet.toAbogado()
            abo.copy(
                especialidades = getEspecialidadesByAbogado(abo.idUsuario)
            )
        } else {
            null
        }

        resultSet.close()
        statement.close()

        return abogado
    }

    override suspend fun findByEspecialidad(especialidadId: Int): List<Abogado> {
        val abogados = mutableListOf<Abogado>()
        val statement = connection.prepareStatement(
            """
            SELECT DISTINCT
                a.id_usuario, a.cedula_profesional, a.biografia, a.calificacion_promedio,
                u.nombre, u.email, u.fecha_registro, u.municipio_id, u.rol_id, u.activo
            FROM Abogados a
            INNER JOIN Usuarios u ON a.id_usuario = u.id_usuario
            INNER JOIN Abogado_Especialidad ae ON a.id_usuario = ae.id_abogado
            WHERE ae.id_catalogo_especialidad = ? AND u.activo = true
            ORDER BY a.calificacion_promedio DESC
            """
        )
        statement.setInt(1, especialidadId)

        val resultSet = statement.executeQuery()
        while (resultSet.next()) {
            val abogado = resultSet.toAbogado()
            abogados.add(abogado.copy(
                especialidades = getEspecialidadesByAbogado(abogado.idUsuario)
            ))
        }

        resultSet.close()
        statement.close()

        return abogados
    }

    override suspend fun create(abogado: Abogado): Abogado? {
        val statement = connection.prepareStatement(
            """
            INSERT INTO Abogados (id_usuario, cedula_profesional, biografia, calificacion_promedio)
            VALUES (?::uuid, ?, ?, ?)
            """
        )

        statement.setString(1, abogado.idUsuario)
        statement.setString(2, abogado.cedulaProfesional)
        statement.setString(3, abogado.biografia)
        statement.setDouble(4, abogado.calificacionPromedio)

        val rowsAffected = statement.executeUpdate()
        statement.close()

        return if (rowsAffected > 0) abogado else null
    }

    override suspend fun update(abogado: Abogado): Abogado? {
        val statement = connection.prepareStatement(
            """
            UPDATE Abogados 
            SET cedula_profesional = ?, biografia = ?
            WHERE id_usuario = ?::uuid
            """
        )

        statement.setString(1, abogado.cedulaProfesional)
        statement.setString(2, abogado.biografia)
        statement.setString(3, abogado.idUsuario)

        val rowsAffected = statement.executeUpdate()
        statement.close()

        return if (rowsAffected > 0) abogado else null
    }

    private fun getEspecialidadesByAbogado(abogadoId: String): List<Especialidad> {
        val especialidades = mutableListOf<Especialidad>()
        val statement = connection.prepareStatement(
            """
            SELECT ce.id, ce.nombre_materia, ce.descripcion
            FROM Catalogo_Especialidad ce
            INNER JOIN Abogado_Especialidad ae ON ce.id = ae.id_catalogo_especialidad
            WHERE ae.id_abogado = ?::uuid
            """
        )
        statement.setString(1, abogadoId)

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

    private fun ResultSet.toAbogado(): Abogado {
        return Abogado(
            idUsuario = getString("id_usuario"),
            cedulaProfesional = getString("cedula_profesional"),
            biografia = getString("biografia"),
            calificacionPromedio = getDouble("calificacion_promedio"),
            usuario = Usuario(
                idUsuario = getString("id_usuario"),
                nombre = getString("nombre"),
                email = getString("email"),
                fechaRegistro = getTimestamp("fecha_registro").toString(),
                municipioId = getInt("municipio_id").let { if (wasNull()) null else it },
                rolId = getInt("rol_id"),
                activo = getBoolean("activo")
            )
        )
    }
}