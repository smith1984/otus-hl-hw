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
    constructor(config: ApplicationConfig, node: String): this(
        url = config.property("${node}.url").getString(),
        user = config.property("${node}.user").getString(),
        password = config.property("${node}.password").getString(),
        schema = config.property("${node}.schema").getString(),
        hikariProperties = config.property("${node}.hikariProperties").getList()
            .associate { prop -> prop.split("_")[0] to prop.split("_")[1] }
    )
}