package ru.beeline.http.request

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.serialization.json.Json
import ru.beeline.http.client.getClients
import ru.beeline.models.IProfile
import ru.beeline.models.IProfileSerializer
import ru.beeline.models.ProfileId
import kotlin.system.measureTimeMillis
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

suspend fun postProfile(
    client: HttpClient,
    body: IProfile,
    hostR: String,
    portR: Int,
    path: String,
): HttpResponse {
    return client.post {
        url {
            host = hostR
            port = portR
            path(path)
        }
        contentType(ContentType.Application.Json)
        setBody(Json.encodeToJsonElement(IProfileSerializer, body))
    }
}

suspend fun getProfile(
    client: HttpClient,
    hostR: String,
    portR: Int,
    path: String,
): HttpResponse {
    return client.get {
        url {
            host = hostR
            port = portR
            path(path)
        }
    }
}

suspend fun requestPerSecond(response: HttpResponse, isAwaitDelay: Boolean): Int {
    var status: Int = 0
    val time = measureTimeMillis {
        status = response.status.value
        if (status != 200) println("status: $status")
    }
    if (isAwaitDelay && time < 1000) delay(1000 - time)
    return status
}

suspend fun parallelRequest(
    numRequestPerSecond: Int,
    isAwaitDelay: Boolean = false,
    timeSendRequest: Duration = 0.seconds,
    cntRequestForClient: Int,
    host: String,
    port: Int,
    path: String,
    lstProfile: List<IProfile>,
    method: String = "post",
) {

    val countClient = 1 + numRequestPerSecond / cntRequestForClient

    val clients = getClients(countClient)

    val requestSemaphore = Semaphore(numRequestPerSecond)

    val allRequest: Int = if (timeSendRequest != 0.seconds)
        numRequestPerSecond
    else
        lstProfile.size

    var allTimeProcess = 0L
    coroutineScope {
        do {
            val time = measureTimeMillis {
                val count = (0 until allRequest).map {
                    async {
                        requestSemaphore.withPermit {
                            requestPerSecond(
                                response = when (method) {

                                    "get" -> {
                                        val iProfile =
                                            if (timeSendRequest == 0.seconds) lstProfile[it] else lstProfile[lstProfile.indices.random()]

                                        val appendPath: String = when (iProfile) {
                                            is ProfileId -> "/${iProfile.id}"
                                            else -> ""
                                        }

                                        getProfile(
                                            client = clients[(clients.indices).random()],
                                            host,
                                            port,
                                            path = path + appendPath
                                        )
                                    }

                                    "post" -> postProfile(
                                        client = clients[(clients.indices).random()],
                                        body = if (timeSendRequest == 0.seconds) lstProfile[it] else lstProfile[lstProfile.indices.random()],
                                        host,
                                        port,
                                        path
                                    )

                                    else -> throw Exception("Unknown method")
                                },
                                isAwaitDelay = isAwaitDelay
                            )
                        }
                    }
                }.awaitAll().count { it == 200 }
                println("Сount with status 200: $count")
            }
            println("Status code (n=$allRequest): $time ms")
            allTimeProcess += time
        } while (timeSendRequest != 0.seconds && allTimeProcess < timeSendRequest.toInt(DurationUnit.MILLISECONDS))
    }

    clients.forEach { client -> client.close() }

}
