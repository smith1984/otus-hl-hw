package ru.beeline

import io.ktor.server.application.*
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import ru.beeline.plugins.configureDB
import ru.beeline.plugins.configureAuth
import ru.beeline.plugins.configureMetrics
import ru.beeline.plugins.configureRouting
import ru.beeline.plugins.configureSerialization

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)


@Suppress("unused")
fun Application.module() {
    val prometheusMeterRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

    //configureDB()
    configureSerialization()
    configureMetrics(prometheusMeterRegistry)
    configureAuth()
    configureRouting(prometheusMeterRegistry)
}
