package ru.beeline.config

import io.ktor.server.config.*


data class JwtConfig(
    val secret: String = "secret",
    val issuer: String = "http://localhost:8080/",
    val audience: String = "http://localhost:8080/user",
    val realm: String = "Access to 'user'",
) {
    constructor(config: ApplicationConfig) : this(
        secret = config.property("jwt.secret").getString(),
        issuer = config.property("jwt.issuer").getString(),
        audience = config.property("jwt.audience").getString(),
        realm = config.property("jwt.realm").getString(),
    )
}