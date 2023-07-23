package ru.beeline.dao

import ru.beeline.models.Auth
import ru.beeline.models.User

interface DAOFacade {
    suspend fun user(id: String): User?
    suspend fun registerUser(user: User, password: String): String?
    suspend fun login(auth: Auth): Boolean?
}