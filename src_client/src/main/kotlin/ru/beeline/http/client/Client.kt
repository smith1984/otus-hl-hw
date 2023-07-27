package ru.beeline.http.client

import io.ktor.client.*
import io.ktor.client.engine.java.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*

fun getClient(): HttpClient =
    HttpClient(Java) {
        //install(Logging)
        install(ContentNegotiation) {
            json()
        }
    }

fun getClients(n: Int): List<HttpClient> = (1 .. n).map { getClient() }