package ru.beeline.plugins

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.*
import ru.beeline.config.PostgresConfig

fun Application.configureDB() {

        val pgConfig = PostgresConfig(environment.config)
        val database = Database.connect(
            pgConfig.url, pgConfig.driver, pgConfig.user, pgConfig.password,
            databaseConfig = DatabaseConfig { },
            setupConnection = { connection ->
                connection.schema = pgConfig.schema
            }
        )
    }