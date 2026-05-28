package com.predicta.app.feature_settings.presentation

import com.predicta.app.feature_settings.domain.model.ThemeMode

sealed interface SettingsEvent {
    data class ChangeTheme(val themeMode: ThemeMode) : SettingsEvent
    data class UpdateName(val name: String) : SettingsEvent
    data class UpdateAvatar(val uri: String) : SettingsEvent
    data class UpdateBaseUrl(val url: String) : SettingsEvent
    data object Logout : SettingsEvent
}
