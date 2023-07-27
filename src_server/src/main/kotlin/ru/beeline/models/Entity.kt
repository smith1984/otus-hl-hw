package ru.beeline.models

import kotlinx.serialization.Serializable
import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date

@Serializable
data class User(
    val id: String,
    val first_name: String,
    val second_name: String,
    val age: Int,
    val birthdate: LocalDate,
    val biography: String,
    val city: String
)

@Serializable
data class UserDTO(
    val first_name: String,
    val second_name: String,
    val age: Int,
    val birthdate: LocalDate,
    val biography: String,
    val city: String,
    val password: String
)

@Serializable
data class Auth(
    val id: String,
    val password: String
)

@Serializable
data class SearchProfileDTO(
    val first_name: String?,
    val second_name: String?
)

@Serializable
data class SearchProfile(
    val firstName: String,
    val secondName: String
)

object Users : Table("users") {
    val id = char("id", 36).uniqueIndex()
    val first_name = text("first_name")
    val second_name = text("second_name")
    val age = integer("age")
    val birthdate = date("birthdate")
    val biography = varchar("biography", 4000)
    val city = varchar("city", 36)
    val pass = varchar("password", 36)

    override val primaryKey = PrimaryKey(id)
}

fun userDTOToUser(userDTO: UserDTO, id: String) =
    User(id, userDTO.first_name, userDTO.second_name, userDTO.age, userDTO.birthdate, userDTO.biography, userDTO.city)

@Serializable
data class ProfileId(val id: String)