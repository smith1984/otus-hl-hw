package ru.beeline

import io.ktor.server.application.*
import io.ktor.server.plugins.openapi.*
import io.ktor.server.routing.*
import ru.beeline.dao.initDB
import ru.beeline.plugins.configureAuth
import ru.beeline.plugins.configureRouting
import ru.beeline.plugins.configureSerialization

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
fun Application.module() {

    routing {
        openAPI(path = "openapi/v1", swaggerFile = "./specs/openapi.json") {
        }
    }

    initDB()
    configureAuth(environment.config)
    configureSerialization()
    configureRouting(environment.config)
}
