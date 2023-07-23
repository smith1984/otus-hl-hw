package ru.beeline

import io.ktor.client.*
import io.ktor.client.engine.java.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlin.system.measureTimeMillis
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit

suspend fun main(args: Array<String>): Unit {

    val numRequests = 300
    val timeInterval = 2.minutes

    val allRequest = numRequests * timeInterval.toInt(DurationUnit.SECONDS)

    val countClient = 1 + numRequests / 100

    val clients = (1..countClient).map {
        HttpClient(Java) {
            install (ContentNegotiation) {
                json()
            }
        }
    }

    suspend fun requestPerSecond(): Int {
        var status: Int = 0
        val time = measureTimeMillis {
            status = clients[(clients.indices).random()].get("http://localhost:8080/health").status.value
            if (status != 200) println("status: $status")
        }
        if (time < 1000) delay(1000 - time)
        return status
    }

    delay(1000)

    val requestSemaphore = Semaphore(numRequests)

    coroutineScope {
        val time = measureTimeMillis {
            val count = (1..allRequest).map {
                async {
                    requestSemaphore.withPermit { requestPerSecond() }
                }
            }.awaitAll().filter { it == 200 }.count()
            println("Get count with status 200: $count")
        }
        println("GET status code (n=$allRequest): $time ms")
    }

    clients.forEach { client -> client.close() }
}

