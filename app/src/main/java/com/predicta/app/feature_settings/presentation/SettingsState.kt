package com.predicta.app.feature_settings.presentation

import com.predicta.app.feature_settings.domain.model.ThemeMode

data class SettingsState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val userName: String = "",
    val email: String = "",
    val role: String = "",
    val isLoggedIn: Boolean = false,
    val avatarUri: String? = null,
    val baseUrl: String = "",
)
