package ru.beeline.plugins

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.*
import ru.beeline.config.PostgresConfig

fun Application.configureDB() {

    val pgConfig = PostgresConfig(environment.config)

    fun createHikariDataSource(
        url: String,
        driver: String,
        user: String,
        pass: String,
    ): HikariDataSource {
        val cfg = HikariConfig().apply {
            driverClassName = driver
            jdbcUrl = url
            username = user
            password = pass
            maximumPoolSize = 20
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"

            validate()
        }

        pgConfig.hikariProperties.forEach { entry -> cfg.addDataSourceProperty(entry.key, entry.value) }

        return HikariDataSource(cfg)
    }

    val ds = createHikariDataSource(
        url = pgConfig.url,
        driver = pgConfig.driver,
        user = pgConfig.user,
        pass = pgConfig.password
    )
    val database = Database.connect(ds)

}