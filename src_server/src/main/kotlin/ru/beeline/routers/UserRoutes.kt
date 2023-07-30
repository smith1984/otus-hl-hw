package ru.beeline.routers

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.get
import io.ktor.server.util.*
import ru.beeline.dao.DAOFacadeImpl
import ru.beeline.models.*
import java.util.*

fun Route.userRouting(dao: DAOFacadeImpl) {

    route("user") {
        post("register") {
            val userDTO = call.receive<UserDTO>()
            val uuid = UUID.randomUUID().toString()

            val user = userDTOToUser(userDTO, uuid)

            val response =
                hashMapOf("user_id" to dao.registerUser(user, userDTO.password)) ?: return@post call.respond(
                    status = HttpStatusCode.ExpectationFailed,
                    hashMapOf("msg" to "Expectation Failed. User not added to DB")
                )
            call.respond(response)
        }

        post("search") {
            val searchProfileDTO = call.receive<SearchProfileDTO>()
            val response = if (searchProfileDTO.first_name == null || searchProfileDTO.second_name == null) {
                return@post call.respond(
                    status = HttpStatusCode.fromValue(400),
                    hashMapOf("msg" to "Invalid data from request")
                )
            } else {
                dao.search(SearchProfile("${searchProfileDTO.first_name}", "${searchProfileDTO.second_name}"))
            }
            call.respond(response)
        }

        get("{id}") {
            val id = call.parameters.getOrFail<String>("id")
            val user = dao.user(id) ?: return@get call.respond(
                status = HttpStatusCode.NotFound,
                hashMapOf("msg" to "Not found user with id $id")
            )
            call.respond(user)
        }
    }
}