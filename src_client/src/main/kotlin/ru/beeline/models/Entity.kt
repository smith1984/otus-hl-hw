package ru.beeline.models

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import ru.beeline.common.readFile

data class FakeData(
    val lstFNM: List<String>,
    val lstLNM: List<String>,
    val lstFNF: List<String>,
    val lstLNF: List<String>,
    val lstCity: List<String>,
    val lstHobby: List<String>,
) {
    constructor(rootPath: String) : this(
        lstCity = readFile("$rootPath/city.txt"),
        lstFNM = readFile("$rootPath/first_name_m.txt"),
        lstLNM = readFile("$rootPath/last_name_m.txt"),
        lstFNF = readFile("$rootPath/first_name_f.txt"),
        lstLNF = readFile("$rootPath/last_name_f.txt"),
        lstHobby = readFile("$rootPath/hobby.txt")
    )
}

interface IProfile

@Serializable
data class User(
    val first_name: String,
    val second_name: String,
    val age: Int,
    val birthdate: LocalDate,
    val biography: String,
    val city: String,
    val password: String,
) : IProfile

@Serializable
data class SearchProfile(
    val first_name: String,
    val second_name: String,
) : IProfile

object IProfileSerializer : JsonContentPolymorphicSerializer<IProfile>(IProfile::class) {
    override fun selectDeserializer(element: JsonElement) = when {
        "age" in element.jsonObject ||
                "birthdate" in element.jsonObject ||
                "biography" in element.jsonObject ||
                "city" in element.jsonObject ||
                "password" in element.jsonObject -> User.serializer()

        "id" in element.jsonObject -> ProfileId.serializer()

        else -> SearchProfile.serializer()
    }
}

@Serializable
data class ProfileId(val id: String): IProfile