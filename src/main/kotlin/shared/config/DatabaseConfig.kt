package com.example.shared.config

import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.util.Properties


object DatabaseConfig {

    // --- Definición de variables leyendo directamente de System.getenv() ---

    // Si la aplicación fallara sin driver, puedes mantenerlo, sino, elimínalo
    private val driver = System.getenv("DB_DRIVER") ?: "org.postgresql.Driver"

    // Obtener las variables inyectadas por Systemd (ahora no se busca el archivo .env)
    private val dbHost = System.getenv("DB_HOST") ?: error("DB_HOST is missing in Systemd Environment")
    private val dbPort = System.getenv("DB_PORT") ?: error("DB_PORT is missing in Systemd Environment")
    private val dbName = System.getenv("DB_NAME") ?: error("DB_NAME is missing in Systemd Environment")

    private val dbUrl = "jdbc:postgresql://$dbHost:$dbPort/$dbName"

    private val user = System.getenv("DB_USERNAME") ?: error("DB_USERNAME is missing in Systemd Environment")
    private val pass = System.getenv("DB_PASSWORD") ?: error("DB_PASSWORD is missing in Systemd Environment")
    init {
        Class.forName(driver)
    }

    fun getConnection(): Connection {
        return DriverManager.getConnection(dbUrl, user, pass)
    }
}
