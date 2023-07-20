package ru.beeline.dao

import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import org.jetbrains.exposed.sql.*
import ru.beeline.models.Auth
import ru.beeline.models.User
import ru.beeline.models.Users

class DAOFacadeImpl : DAOFacade {

    private fun resultRowToUser(row: ResultRow) = User(
        id = row[Users.id],
        first_name = row[Users.first_name],
        second_name = row[Users.second_name],
        age = row[Users.age],
        birthdate = row[Users.birthdate].toKotlinLocalDate(),
        biography = row[Users.biography],
        city = row[Users.city],
    )

    override suspend fun user(id: String): User? = dbQuery {
        Users
            .select { Users.id eq id }
            .map(::resultRowToUser)
            .singleOrNull()
    }

    override suspend fun registerUser(user: User, password: String): String? = dbQuery {
        val insertStatementUser = Users.insert {
            it[id] = user.id
            it[first_name] = user.first_name
            it[second_name] = user.second_name
            it[age] = user.age
            it[birthdate] = user.birthdate.toJavaLocalDate()
            it[biography] = user.biography
            it[city] = user.city
            it[pass] = password
        }

        insertStatementUser.resultedValues?.singleOrNull()?.let(::resultRowToUser)?.id
    }

    override suspend fun login(auth: Auth): Boolean? = dbQuery {
        val passwordRow = Users.slice(Users.pass).select { Users.id eq auth.id }.singleOrNull()
        return@dbQuery if (passwordRow != null) {
            val password = passwordRow[Users.pass]
            password == auth.password
        } else
            null
    }
}

val dao: DAOFacade = DAOFacadeImpl()