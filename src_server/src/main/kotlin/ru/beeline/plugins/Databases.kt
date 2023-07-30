package ru.beeline.plugins

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.*
import ru.beeline.config.PostgresConfig

fun Application.configureDB() {

    val pgConfigMaster = PostgresConfig(environment.config, "psql")

    val dsMaster = createHikariDataSource(pgConfigMaster)

    val database = Database.connect(dsMaster)
}

fun createHikariDataSource(pgConfig: PostgresConfig
): HikariDataSource {
    val cfg = HikariConfig().apply {
        driverClassName = pgConfig.driver
        jdbcUrl = pgConfig.url
        username = pgConfig.user
        password = pgConfig.password
        maximumPoolSize = 20
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"

        validate()
    }

    pgConfig.hikariProperties.forEach { entry -> cfg.addDataSourceProperty(entry.key, entry.value) }

    return HikariDataSource(cfg)
}