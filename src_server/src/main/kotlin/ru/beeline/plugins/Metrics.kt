package ru.beeline.plugins

import io.ktor.server.application.*
import io.ktor.server.metrics.micrometer.*
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics
import io.micrometer.core.instrument.binder.system.ProcessorMetrics
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import java.time.Duration

fun Application.configureMetrics(prometheusMeterRegistry: PrometheusMeterRegistry) {

    install(MicrometerMetrics) {
        registry = prometheusMeterRegistry
        distributionStatisticConfig = DistributionStatisticConfig.Builder()
            .percentilesHistogram(true)
            .maximumExpectedValue(Duration.ofSeconds(20).toNanos().toDouble())
            .serviceLevelObjectives(
                Duration.ofMillis(10).toNanos().toDouble(),
                Duration.ofMillis(50).toNanos().toDouble(),
                Duration.ofMillis(100).toNanos().toDouble(),
                Duration.ofMillis(200).toNanos().toDouble(),
                Duration.ofMillis(300).toNanos().toDouble(),
                Duration.ofMillis(400).toNanos().toDouble(),
                Duration.ofMillis(500).toNanos().toDouble()
            )
            .build()
        meterBinders = listOf(
            JvmMemoryMetrics(),
            JvmGcMetrics(),
            ProcessorMetrics()
        )
    }
}