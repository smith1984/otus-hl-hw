package ru.beeline.plugins

import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import io.ktor.server.plugins.openapi.*
import io.micrometer.prometheus.PrometheusMeterRegistry
import ru.beeline.config.JwtConfig
import ru.beeline.dao.DAOFacadeImpl
import ru.beeline.routers.loginRouting
import ru.beeline.routers.userRouting

fun Application.configureRouting(prometheusMeterRegistry: PrometheusMeterRegistry) {
    val jwtCfg = JwtConfig(environment.config)
    val dao = DAOFacadeImpl(environment.config)

    routing {
        get("/health") {
            call.respond(mapOf("status" to "ok"))
        }

        get("/actuator/prometheus") {
            call.respond(prometheusMeterRegistry.scrape())
        }

        openAPI(path = "openapi/v1", swaggerFile = "./specs/openapi.json") {
        }

        loginRouting(jwtCfg, dao)
        userRouting(dao)
    }
}
