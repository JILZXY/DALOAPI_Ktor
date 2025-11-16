package com.example.shared.config

import java.sql.Connection
import java.sql.DriverManager

object DatabaseConfig {

    private const val DRIVER = "org.postgresql.Driver"
    private const val JDBC_URL = "jdbc:postgresql://localhost:5432/legalapp_db"
    private const val DB_USER = "postgres"
    private const val DB_PASSWORD = "password"

    init {
        Class.forName(DRIVER)
    }

    fun getConnection(): Connection {
        return DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD)
    }
}