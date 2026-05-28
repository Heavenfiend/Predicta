package com.predicta.app.feature_auth.data.repository

import com.predicta.app.core.error.AppError
import com.predicta.app.core.error.AppResult
import com.predicta.app.data.remote.PredictaApi
import com.predicta.app.data.remote.dto.LoginRequest
import com.predicta.app.data.remote.dto.RegisterRequest
import com.predicta.app.data.remote.dto.ErrorResponse
import com.predicta.app.feature_auth.data.session.UserSessionManager
import com.predicta.app.feature_auth.domain.model.User
import com.predicta.app.feature_auth.domain.repository.AuthRepository
import kotlinx.serialization.json.Json
import retrofit2.HttpException

class AuthRepositoryImpl(
    private val api: PredictaApi,
    private val json: Json,
    private val sessionManager: UserSessionManager,
) : AuthRepository {

    override suspend fun login(email: String, password: String): AppResult<User> {
        return try {
            val response = api.login(LoginRequest(email = email, password = password))
            val token = response.token
            
            // Temporarily store session to let interceptor access the token
            sessionManager.startSession(
                User(
                    email = email,
                    name = email,
                    token = token
                )
            )
            
            // Get full user details
            val me = api.getMe()
            val user = User(
                id = "user_me",
                email = me.email,
                name = "${me.firstName} ${me.lastName}".trim(),
                role = "manager",
                token = token,
                telegramNick = me.telegramNick,
                phone = me.phone,
                avatarUrl = me.avatarUrl,
                subordinatesCount = me.subordinatesCount
            )
            
            AppResult.Success(user)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val message = errorBody?.let {
                runCatching { json.decodeFromString<ErrorResponse>(it).error }.getOrNull()
            }
            when (e.code()) {
                401 -> AppResult.Failure(AppError.Auth)
                else -> AppResult.Failure(AppError.Unknown(message ?: e.message()))
            }
        } catch (e: Exception) {
            AppResult.Failure(AppError.Network)
        }
    }

    override suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        telegramNick: String,
        phone: String,
        avatarUrl: String?,
    ): AppResult<Unit> {
        return try {
            api.register(
                RegisterRequest(
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                    password = password,
                    telegramNick = telegramNick,
                    phone = phone,
                    avatarUrl = avatarUrl,
                )
            )
            AppResult.Success(Unit)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val message = errorBody?.let {
                runCatching { json.decodeFromString<ErrorResponse>(it).error }.getOrNull()
            }
            when (e.code()) {
                409 -> AppResult.Failure(AppError.Unknown(message ?: "Пользователь уже зарегистрирован"))
                400 -> AppResult.Failure(AppError.Unknown(message ?: "Некорректные данные"))
                else -> AppResult.Failure(AppError.Unknown(message ?: e.message()))
            }
        } catch (e: Exception) {
            AppResult.Failure(AppError.Network)
        }
    }
}
