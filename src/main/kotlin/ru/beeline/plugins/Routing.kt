package ru.beeline.plugins

import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import ru.beeline.config.JwtConfig
import ru.beeline.routers.loginRouting
import ru.beeline.routers.userRouting

fun Application.configureRouting(cfg: ApplicationConfig) {
    val jwtCfg = JwtConfig(cfg)

    routing {
        get("/health") {
            call.respond(mapOf("status" to "ok"))
        }
        loginRouting(jwtCfg)
        userRouting()
    }
}
