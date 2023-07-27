package ru.beeline.dao

import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import ru.beeline.models.*

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

    override suspend fun search(searchProfile: SearchProfile): List<User> = dbQuery {
        Users.slice(
            Users.first_name,
            Users.second_name,
            Users.id,
            Users.age,
            Users.birthdate,
            Users.biography,
            Users.city
        )
            .select { Users.first_name like "${searchProfile.firstName}%" and (Users.second_name like "${searchProfile.secondName}%") }
            .orderBy(Users.id)
            .map(::resultRowToUser)
    }

}

val dao: DAOFacade = DAOFacadeImpl()

suspend fun <T> dbQuery(block: suspend () -> T): T =
    newSuspendedTransaction(Dispatchers.IO) { block() }