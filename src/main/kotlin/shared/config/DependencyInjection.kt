package com.example.shared.config

import com.example.estado.application.EstadoService
import com.example.estado.domain.port.Service.EstadoServicePort
import com.example.estado.domain.port.Repository.EstadoRepositoryPort
import com.example.estado.infrastructure.persistence.EstadoRepositoryAdapter
import com.example.estado.infrastructure.web.EstadoController
import com.example.municipio.application.MunicipioService
import com.example.municipio.infrastructure.web.MunicipioController
import com.example.municipio.domain.port.Service.MunicipioServicePort
import com.example.municipio.domain.port.Repository.MunicipioRepositoryPort
import com.example.municipio.infrastructure.persistence.MunicipioRepositoryAdapter
import com.example.usuario.application.UsuarioService
import com.example.usuario.application.AbogadoService
import com.example.usuario.infrastructure.persistence.UsuarioRepositoryAdapter
import com.example.usuario.infrastructure.persistence.AbogadoRepositoryAdapter
import com.example.usuario.infrastructure.web.UsuarioController
import com.example.usuario.infrastructure.web.AbogadoController
import com.example.consulta.infrastructure.web.ConsultaController
import com.example.consulta.application.ConsultaService
import com.example.consulta.domain.port.Service.ConsultaServicePort
import com.example.consulta.domain.port.Repository.ConsultaRepositoryPort
import com.example.consulta.infrastructure.persistence.ConsultaRepositoryAdapter
import com.example.consulta.infrastructure.web.RespuestaConsultaController
import com.example.consulta.application.RespuestaConsultaService
import com.example.consulta.domain.port.Service.RespuestaConsultaServicePort
import com.example.consulta.domain.port.Repository.RespuestaConsultaRepositoryPort
import com.example.consulta.infrastructure.persistence.RespuestaConsultaRepositoryAdapter
import com.example.reporte.application.ReporteService
import com.example.reporte.domain.port.Repository.MotivoReporteRepositoryPort
import com.example.reporte.domain.port.Repository.ReporteRepositoryPort
import com.example.reporte.domain.port.Service.ReporteServicePort
import com.example.reporte.infrastructure.persistence.MotivoReporteRepositoryAdapter
import com.example.reporte.infrastructure.persistence.ReporteRepositoryAdapter
import com.example.reporte.infrastructure.web.ReporteController
import com.example.calificacion.application.CalificacionService
import com.example.calificacion.domain.port.Repository.CalificacionRepositoryPort
import com.example.calificacion.domain.port.Service.CalificacionServicePort
import com.example.calificacion.infrastructure.persistence.CalificacionRepositoryAdapter
import com.example.calificacion.infrastructure.web.CalificacionController
import com.example.bufete.application.BufeteService
import com.example.bufete.domain.port.Repository.BufeteRepositoryPort
import com.example.bufete.domain.port.Service.BufeteServicePort
import com.example.bufete.infrastructure.web.BufeteController
import com.example.bufete.infrastructure.persistence.BufeteRepositoryAdapter
import com.example.bufete.application.SolicitudBufeteService
import com.example.bufete.domain.port.Repository.SolicitudBufeteRepositoryPort
import com.example.bufete.domain.port.Service.SolicitudBufeteServicePort
import com.example.bufete.infrastructure.persistence.SolicitudBufeteRepositoryAdapter
import com.example.bufete.infrastructure.web.SolicitudBufeteController
import com.example.bufete.application.CalificacionBufeteService
import com.example.bufete.domain.port.Repository.CalificacionBufeteRepositoryPort
import com.example.bufete.domain.port.Service.CalificacionBufeteServicePort
import com.example.bufete.infrastructure.persistence.CalificacionBufeteRepositoryAdapter
import com.example.bufete.infrastructure.web.CalificacionBufeteController
import com.example.chat.application.ChatService
import com.example.chat.domain.port.Repository.ChatRepositoryPort
import com.example.chat.domain.port.Service.ChatServicePort
import com.example.chat.infrastructure.persistence.ChatRepositoryAdapter
import com.example.chat.infrastructure.web.ChatController
import com.example.chat.application.MensajeService
import com.example.chat.domain.port.Repository.MensajeRepositoryPort
import com.example.chat.domain.port.Service.MensajeServicePort
import com.example.chat.infrastructure.persistence.MensajeRepositoryAdapter
import com.example.chat.infrastructure.web.MensajeController
import com.example.shared.security.JwtConfig
import com.example.shared.security.PasswordHasher
import com.example.usuario.domain.port.Repository.AbogadoRepositoryPort
import com.example.usuario.domain.port.Repository.UsuarioRepositoryPort
import com.example.usuario.domain.port.Service.AbogadoServicePort
import com.example.usuario.domain.port.Service.UsuarioServicePort
import java.sql.Connection


object DependencyInjection {

    private val connection: Connection by lazy {
        DatabaseConfig.getConnection()
    }

    // Security
    val jwtConfig: JwtConfig by lazy {
        JwtConfig()
    }

    val passwordHasher: PasswordHasher by lazy {
        PasswordHasher()
    }
    // Estado
    private val estadoRepository: EstadoRepositoryPort by lazy {
        EstadoRepositoryAdapter(connection)
    }

    val estadoService: EstadoServicePort by lazy {
        EstadoService(estadoRepository)
    }

    val estadoController: EstadoController by lazy {
        EstadoController(estadoService)
    }

    //Municipio
    private val municipioRepository: MunicipioRepositoryPort by lazy {
        MunicipioRepositoryAdapter(connection)
    }

    val municipioService: MunicipioServicePort by lazy {
        MunicipioService(municipioRepository)
    }

    val municipioController: MunicipioController by lazy {
        MunicipioController(municipioService)
    }

    //Usuario
    private val usuarioRepository: UsuarioRepositoryPort by lazy {
        UsuarioRepositoryAdapter(connection)
    }

    val usuarioService: UsuarioServicePort by lazy {
        UsuarioService(usuarioRepository, passwordHasher, jwtConfig)
    }

    val usuarioController: UsuarioController by lazy {
        UsuarioController(usuarioService)
    }

    //Abogado
    private val abogadoRepository: AbogadoRepositoryPort by lazy {
        AbogadoRepositoryAdapter(connection)
    }

    val abogadoService: AbogadoServicePort by lazy {
        AbogadoService(abogadoRepository)
    }

    val abogadoController: AbogadoController by lazy {
        AbogadoController(abogadoService)
    }

    //Consulta
    private val consultaRepository: ConsultaRepositoryPort by lazy {
        ConsultaRepositoryAdapter(connection)
    }

    val consultaService: ConsultaServicePort by lazy {
        ConsultaService(consultaRepository)
    }

    val consultaController: ConsultaController by lazy {
        ConsultaController(consultaService)
    }

    //RespuesConsulta
    private val respuestaConsultaRepository: RespuestaConsultaRepositoryPort by lazy {
        RespuestaConsultaRepositoryAdapter(connection)
    }

    val respuestaConsultaService: RespuestaConsultaServicePort by lazy {
        RespuestaConsultaService(respuestaConsultaRepository)
    }

    val respuestaConsultaController: RespuestaConsultaController by lazy {
        RespuestaConsultaController(respuestaConsultaService)
    }

    // ===== CALIFICACION MODULE =====
    private val calificacionRepository: CalificacionRepositoryPort by lazy {
        CalificacionRepositoryAdapter(connection)
    }

    val calificacionService: CalificacionServicePort by lazy {
        CalificacionService(calificacionRepository)
    }

    val calificacionController: CalificacionController by lazy {
        CalificacionController(calificacionService)
    }

    // ===== BUFETE MODULE =====
    private val bufeteRepository: BufeteRepositoryPort by lazy {
        BufeteRepositoryAdapter(connection)
    }

    val bufeteService: BufeteServicePort by lazy {
        BufeteService(bufeteRepository)
    }

    val bufeteController: BufeteController by lazy {
        BufeteController(bufeteService)
    }

    // ===== SOLICITUD BUFETE MODULE =====
    private val solicitudBufeteRepository: SolicitudBufeteRepositoryPort by lazy {
        SolicitudBufeteRepositoryAdapter(connection)
    }

    val solicitudBufeteService: SolicitudBufeteServicePort by lazy {
        SolicitudBufeteService(solicitudBufeteRepository, bufeteRepository, solicitudBufeteRepository)
    }

    val solicitudBufeteController: SolicitudBufeteController by lazy {
        SolicitudBufeteController(solicitudBufeteService, bufeteService)
    }

    // ===== CALIFICACION BUFETE MODULE =====
    private val calificacionBufeteRepository: CalificacionBufeteRepositoryPort by lazy {
        CalificacionBufeteRepositoryAdapter(connection)
    }

    val calificacionBufeteService: CalificacionBufeteServicePort by lazy {
        CalificacionBufeteService(calificacionBufeteRepository)
    }

    val calificacionBufeteController: CalificacionBufeteController by lazy {
        CalificacionBufeteController(calificacionBufeteService)
    }

    // ===== CHAT MODULE =====
    private val chatRepository: ChatRepositoryPort by lazy {
        ChatRepositoryAdapter(connection)
    }

    val chatService: ChatServicePort by lazy {
        ChatService(chatRepository)
    }

    val chatController: ChatController by lazy {
        ChatController(chatService)
    }

    // ===== MENSAJE MODULE =====
    private val mensajeRepository: MensajeRepositoryPort by lazy {
        MensajeRepositoryAdapter(connection)
    }

    val mensajeService: MensajeServicePort by lazy {
        MensajeService(mensajeRepository)
    }

    val mensajeController: MensajeController by lazy {
        MensajeController(mensajeService, chatService)
    }

    // ===== REPORTE MODULE =====
    private val reporteRepository: ReporteRepositoryPort by lazy {
        ReporteRepositoryAdapter(connection)
    }

    private val motivoReporteRepository: MotivoReporteRepositoryPort by lazy {
        MotivoReporteRepositoryAdapter(connection)
    }

    val reporteService: ReporteServicePort by lazy {
        ReporteService(reporteRepository, motivoReporteRepository)
    }

    val reporteController: ReporteController by lazy {
        ReporteController(reporteService)
    }
}