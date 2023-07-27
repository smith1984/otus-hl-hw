package ru.beeline.generator

import kotlinx.datetime.toKotlinLocalDate
import ru.beeline.models.FakeData
import ru.beeline.models.SearchProfile
import ru.beeline.models.User
import java.time.LocalDate
import java.time.Period
import java.util.concurrent.ThreadLocalRandom
import kotlin.random.Random

fun getProfiles(n: Int, data: FakeData): List<User> = (1..n).map { getProfile(data) }

private fun getProfile(
    data: FakeData,
): User {
    val sex = Random.nextBoolean()

    val (firstName, lastName) = if (sex) {
        getName(data.lstFNM, data.lstLNM)
    } else {
        getName(data.lstFNF, data.lstLNF)
    }

    val hobbies = getNRandomElements(data.lstHobby)

    val city = getRandomElement(data.lstCity)

    val birthdate = getBirthdate()

    val age = getAge(birthdate)

    val password = getRandPassword(10)

    return User(
        first_name = firstName,
        second_name = lastName,
        age = age,
        birthdate = birthdate.toKotlinLocalDate(),
        biography = hobbies.joinToString(),
        city = city,
        password = password
    )
}

private fun getRandomElement(lst: List<String>) = lst[lst.indices.random()]

private fun getNRandomElements(lst: List<String>) = (1..(1..lst.size).random()).map { getRandomElement(lst) }.toSet()

private fun getName(lstFN: List<String>, lstLN: List<String>): Pair<String, String> {
    return getRandomElement(lstFN) to getRandomElement(lstLN)
}

private fun getBirthdate(): LocalDate {
    val start: LocalDate = LocalDate.now().minusYears(100)
    val end: LocalDate = LocalDate.now().minusYears(18)
    return LocalDate.ofEpochDay(ThreadLocalRandom.current().nextLong(start.toEpochDay(), end.toEpochDay()))
}

private fun getAge(birthdate: LocalDate): Int =
    Period.between(birthdate, LocalDate.now()).years

private fun getRandPassword(n: Int): String {
    val characterSet = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_@#$"

    val random = Random(System.nanoTime())
    val password = StringBuilder()

    for (i in 0 until n) {
        val rIndex = random.nextInt(characterSet.length)
        password.append(characterSet[rIndex])
    }

    return password.toString()
}

fun getConditionForSearch(fakeData: FakeData, isFullName: Boolean = false): List<SearchProfile> {

    val fnm = fakeData.lstFNM.map { dropLastRandomChar(it, isFullName) }
    val lnm = fakeData.lstLNM.map { dropLastRandomChar(it, isFullName) }

    val fnf = fakeData.lstFNF.map { dropLastRandomChar(it, isFullName) }
    val lnf = fakeData.lstLNF.map { dropLastRandomChar(it, isFullName) }

    return getCrossJoin(fnm, lnm).union(getCrossJoin(fnf, lnf)).distinct()
        .map { pair -> SearchProfile(pair.first, pair.second) }
}

private fun dropLastRandomChar(str: String, isFullName: Boolean): String =
    if (isFullName) str else str.dropLast((0 until str.length - 1).random())


private fun getCrossJoin(lstFirst: List<String>, lstSecond: List<String>): List<Pair<String, String>> =
    lstFirst.map { fe -> lstSecond.map { se -> Pair(fe, se) } }.flatten().distinct()