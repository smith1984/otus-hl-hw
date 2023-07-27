package ru.beeline.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import ru.beeline.config.JwtConfig

fun Application.configureAuth() {
    val jwtCfg = JwtConfig(environment.config)

    install(Authentication) {
        jwt("auth-jwt") {
            realm = jwtCfg.realm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtCfg.secret))
                    .withAudience(jwtCfg.audience)
                    .withIssuer(jwtCfg.issuer)
                    .build()
            )

            validate { credential ->
                if (credential.payload.getClaim("username").asString() != "") {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }
}