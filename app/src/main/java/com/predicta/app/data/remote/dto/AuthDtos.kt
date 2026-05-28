package com.predicta.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ── Register ────────────────────────────────────────────────────────────────

@Serializable
data class RegisterRequest(
    @SerialName("first_name") val firstName: String,
    @SerialName("last_name") val lastName: String,
    val email: String,
    val password: String,
    @SerialName("telegram_nick") val telegramNick: String,
    val phone: String,
    @SerialName("avatar_url") val avatarUrl: String? = null,
)

@Serializable
data class RegisterResponse(
    val message: String,
)

// ── Login ───────────────────────────────────────────────────────────────────

@Serializable
data class LoginRequest(
    val email: String,
    val password: String,
)

@Serializable
data class LoginResponse(
    val token: String,
)

// ── Me ──────────────────────────────────────────────────────────────────────

@Serializable
data class MeResponse(
    @SerialName("first_name") val firstName: String,
    @SerialName("last_name") val lastName: String,
    val email: String,
    @SerialName("telegram_nick") val telegramNick: String = "",
    val phone: String = "",
    @SerialName("subordinates_count") val subordinatesCount: Int = 0,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    @SerialName("jira_display_name") val jiraDisplayName: String? = null,
    @SerialName("jira_email") val jiraEmail: String? = null,
)
