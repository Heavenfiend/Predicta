package com.predicta.app.feature_auth.domain.model

data class User(
    val id: String = "",
    val email: String,
    val name: String,
    val role: String = "manager",
    val token: String = "",
    val telegramNick: String = "",
    val phone: String = "",
    val avatarUrl: String? = null,
    val subordinatesCount: Int = 0,
)
