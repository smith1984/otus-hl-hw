package ru.beeline.config

import io.ktor.server.config.*


data class PostgresConfig(
    val url: String = "jdbc:postgresql://localhost:5432/db_test",
    val user: String = "user",
    val password: String = "password",
    val schema: String = "public",
    val driver: String = "org.postgresql.Driver",
    val hikariProperties: Map<String, String> = mapOf()
) {
    constructor(config: ApplicationConfig): this(
        url = config.property("psql.url").getString(),
        user = config.property("psql.user").getString(),
        password = config.property("psql.password").getString(),
        schema = config.property("psql.schema").getString(),
        hikariProperties = config.property("psql.hikariProperties").getList()
            .associate { prop -> prop.split("_")[0] to prop.split("_")[1] }
    )
}