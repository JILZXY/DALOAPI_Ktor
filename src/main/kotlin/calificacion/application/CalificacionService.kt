package com.example.calificacion.application

import com.example.calificacion.domain.model.Calificacion
import com.example.calificacion.domain.model.CalificacionPromedio
import com.example.calificacion.domain.model.CreateCalificacionRequest
import com.example.calificacion.domain.model.PromediosRespuesta
import com.example.calificacion.domain.port.Service.CalificacionServicePort
import com.example.calificacion.domain.port.Repository.CalificacionRepositoryPort

class CalificacionService(
    private val calificacionRepository: CalificacionRepositoryPort
) : CalificacionServicePort {

    override suspend fun getCalificacionesByAbogadoId(abogadoId: String): List<Calificacion> {
        return calificacionRepository.findByAbogadoId(abogadoId)
    }

    override suspend fun getCalificacionById(id: Int): Calificacion? {
        return calificacionRepository.findById(id)
    }

    override suspend fun getPromediosByAbogado(abogadoId: String): CalificacionPromedio {
        return calificacionRepository.getPromediosByAbogado(abogadoId)
    }

    override suspend fun getPromedioGeneralAbogado(abogadoId: String): Double {
        val promedios = calificacionRepository.getPromediosByAbogado(abogadoId)
        return promedios.promedioGeneral
    }

    override suspend fun getPromediosPorRespuesta(respuestaId: Int): PromediosRespuesta {
        return calificacionRepository.getPromediosByRespuestaId(respuestaId)
    }

    override suspend fun getPromediosByRespuestaId(respuestaId: Int): PromediosRespuesta {
        val statement = connection.prepareStatement(
            """
            SELECT 
                rc.id_respuesta,
                AVG(c.atencion) as atencion_promedio,
                AVG(c.profesionalismo) as profesionalismo_promedio,
                AVG(c.claridad) as claridad_promedio,
                AVG(c.empatia) as empatia_promedio,
                COUNT(*) as total_calificaciones
            FROM Respuesta_Consulta rc
            INNER JOIN Calificaciones c ON rc.id_abogado = c.id_abogado
            WHERE rc.id_respuesta = ?
            GROUP BY rc.id_respuesta
            """
        )
        statement.setInt(1, respuestaId)

        val resultSet = statement.executeQuery()
        val promedios = if (resultSet.next()) {
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
            val total = resultSet.getInt("total_calificaciones")

            // Calcular promedio general
            val promedioGeneral = if (total > 0) {
                (atencionProm + profesionalismoProm + claridadProm + empatiaProm) / 4.0
            } else {
                0.0
            }

            PromediosRespuesta(
                idRespuesta = respuestaId,
                atencionPromedio = atencionProm,
                profesionalismoPromedio = profesionalismoProm,
                claridadPromedio = claridadProm,
                empatiaPromedio = empatiaProm,
                promedioGeneral = promedioGeneral,
                totalCalificaciones = total
            )
        } else {
            // Si no hay calificaciones para esta respuesta
            PromediosRespuesta(
                idRespuesta = respuestaId,
                atencionPromedio = 0.0,
                profesionalismoPromedio = 0.0,
                claridadPromedio = 0.0,
                empatiaPromedio = 0.0,
                promedioGeneral = 0.0,
                totalCalificaciones = 0
            )
        }

        resultSet.close()
        statement.close()

        return promedios
    }

    override suspend fun createCalificacion(
        usuarioId: String,
        abogadoId: String,
        request: CreateCalificacionRequest
    ): Calificacion? {
        // Validar que las calificaciones estén entre 1 y 5
        if (request.atencion !in 1..5 || request.profesionalismo !in 1..5 ||
            request.claridad !in 1..5 || request.empatia !in 1..5) {
            return null
        }

        val calificacion = Calificacion(
            idUsuario = usuarioId,
            idAbogado = abogadoId,
            atencion = request.atencion,
            profesionalismo = request.profesionalismo,
            claridad = request.claridad,
            empatia = request.empatia,
            comentarioOpcional = request.comentarioOpcional
        )

        val created = calificacionRepository.create(calificacion)

        // Actualizar promedio del abogado
        if (created != null) {
            calificacionRepository.updateAbogadoCalificacionPromedio(abogadoId)
        }

        return created
    }
}