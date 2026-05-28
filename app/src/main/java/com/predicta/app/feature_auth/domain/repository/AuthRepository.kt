package com.predicta.app.feature_auth.domain.repository

import com.predicta.app.core.error.AppResult
import com.predicta.app.feature_auth.domain.model.User

interface AuthRepository {
    suspend fun login(email: String, password: String): AppResult<User>
    suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        telegramNick: String,
        phone: String,
        avatarUrl: String? = null,
    ): AppResult<Unit>
}
