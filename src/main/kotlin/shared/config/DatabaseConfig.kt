package com.example.shared.config

import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.util.Properties


object DatabaseConfig {

    private val props = Properties().apply {
        load(File(".env").inputStream())
    }

    private val driver = props.getProperty("DB_DRIVER") ?: "org.postgresql.Driver"
    private val dbHost = props.getProperty("DB_HOST") ?: error("DB_HOST is missing in .env")
    private val dbPort = props.getProperty("DB_PORT") ?: error("DB_PORT is missing in .env")
    private val dbName = props.getProperty("DB_NAME") ?: error("DB_NAME is missing in .env")
    private val dbUrl = "jdbc:postgresql://$dbHost:$dbPort/$dbName"
    private val user = props.getProperty("DB_USERNAME") ?: error("DB_USERNAME is missing in .env")
    private val pass = props.getProperty("DB_PASSWORD") ?: error("DB_PASSWORD is missing in .env")

    init {
        Class.forName(driver)
    }

    fun getConnection(): Connection {
        return DriverManager.getConnection(dbUrl, user, pass)
    }
}
