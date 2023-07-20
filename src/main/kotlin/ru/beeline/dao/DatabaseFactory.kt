package ru.beeline.dao

import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import ru.beeline.config.PostgresConfig

fun Application.initDB() {

        val pgConfig = PostgresConfig(environment.config)
        val database = Database.connect(
            pgConfig.url, pgConfig.driver, pgConfig.user, pgConfig.password,
            databaseConfig = DatabaseConfig { },
            setupConnection = { connection ->
                connection.schema = pgConfig.schema
            }
        )
    }

suspend fun <T> dbQuery(block: suspend () -> T): T =
    newSuspendedTransaction(Dispatchers.IO) { block() }