package com.example

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.shared.config.DependencyInjection
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        // Health check
        get("/") {
            call.respondText("Legal App API is running")
        }

        // Estados routes
        with(DependencyInjection.estadoController) {
            estadoRoutes()
        }

        // Municipios routes
        with(DependencyInjection.municipioController) {
            municipioRoutes()
        }

        // Usuarios routes (auth + usuarios)
        with(DependencyInjection.usuarioController) {
            usuarioRoutes()
        }

        // Abogados routes
        with(DependencyInjection.abogadoController) {
            abogadoRoutes()
        }

        // Consultas routes
        with(DependencyInjection.consultaController) {
            consultaRoutes()
        }

        // Respuestas routes
        with(DependencyInjection.respuestaConsultaController) {
            respuestaConsultaRoutes()
        }

        // ===== BUFETE ROUTES =====
        with(DependencyInjection.bufeteController) {
            bufeteRoutes()
        }

        // ===== SOLICITUD BUFETE ROUTES =====
        with(DependencyInjection.solicitudBufeteController) {
            solicitudBufeteRoutes()
        }

        // ===== CALIFICACION BUFETE ROUTES =====
        with(DependencyInjection.calificacionBufeteController) {
            calificacionBufeteRoutes()
        }

        // ===== CHAT ROUTES =====
        with(DependencyInjection.chatController) {
            chatRoutes()
        }

        // ===== MENSAJE ROUTES =====
        with(DependencyInjection.mensajeController) {
            mensajeRoutes()
        }

        // ===== REPORTE ROUTES =====
        with(DependencyInjection.reporteController) {
            reporteRoutes()
        }
    }
}
