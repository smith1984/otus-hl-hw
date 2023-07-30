package ru.beeline.routers

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.beeline.config.JwtConfig
import ru.beeline.dao.DAOFacadeImpl
import ru.beeline.models.Auth
import java.util.*

fun Route.loginRouting(cfg: JwtConfig, dao: DAOFacadeImpl) {

    route("/login") {
        post {
            val auth =  call.receive<Auth>()

            val resultAuth = dao.login(auth)

            if (resultAuth != null ){
                if(resultAuth) {
                    val token = JWT.create()
                        .withAudience(cfg.audience)
                        .withIssuer(cfg.issuer)
                        .withClaim("username", auth.id)
                        .withExpiresAt(Date(System.currentTimeMillis() + 3000000))
                        .sign(Algorithm.HMAC256(cfg.secret))
                    call.respond(hashMapOf("token" to token))
                }
                else
                    call.respond(status = HttpStatusCode.fromValue(400),
                        hashMapOf("msg" to "Incorrect password"))
            }
            else
                call.respond(
                    status = HttpStatusCode.NotFound,
                    hashMapOf("msg" to "User not found")
                )
        }
    }

}