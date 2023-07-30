package ru.beeline

import ru.beeline.common.readFile
import ru.beeline.generator.getConditionForSearch
import ru.beeline.generator.getProfiles
import ru.beeline.http.request.parallelRequest
import ru.beeline.models.FakeData
import ru.beeline.models.ProfileId
import kotlin.time.Duration.Companion.seconds

suspend fun main(args: Array<String>): Unit {

    val rootPath = "src_client/src/main/resources"
    val fakeData = FakeData(rootPath)

// Создание 1 000 000 анкет
//    val profiles = getProfiles(50000, fakeData)
//    parallelRequest(
//        numRequestPerSecond = 100,
//        isAwaitDelay = false,
//        cntRequestForClient = 100,
//        host = "localhost",
//        port = 8081,
//        path = "user/register",
//        lstProfile = profiles
//    )

// Нагрузочное тестирование поиска анкет
//    val dataForSearch = getConditionForSearch(fakeData, isFullName = false)
//
//    parallelRequest(
//        numRequestPerSecond = 100,
//        isAwaitDelay = true,
//        timeSendRequest = 360.seconds,
//        cntRequestForClient = 100,
//        host = "localhost",
//        port = 8081,
//        path = "user/search",
//        lstProfile = dataForSearch
//    )

// Нагрузочное тестирование поиска пользователя по id
//    val lstId = readFile("${rootPath}/user_id.txt").map { ProfileId(it) }
//
//    parallelRequest(
//        numRequestPerSecond = 100,
//        isAwaitDelay = true,
//        timeSendRequest = 360.seconds,
//        cntRequestForClient = 100,
//        host = "localhost",
//        port = 8081,
//        path = "user",
//        lstProfile = lstId,
//        method = "get"
//    )
}