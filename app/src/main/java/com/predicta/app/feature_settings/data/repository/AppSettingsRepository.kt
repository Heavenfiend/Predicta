package com.predicta.app.feature_settings.data.repository

import android.content.Context
import com.predicta.app.feature_settings.domain.model.AppSettings
import com.predicta.app.feature_settings.domain.model.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AppSettingsRepository(context: Context) {

    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(
        AppSettings(
            themeMode = ThemeMode.fromStorageValue(
                preferences.getString(KEY_THEME_MODE, ThemeMode.SYSTEM.storageValue),
            ),
        ),
    )
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    fun setThemeMode(themeMode: ThemeMode) {
        preferences.edit()
            .putString(KEY_THEME_MODE, themeMode.storageValue)
            .apply()

        _settings.update { it.copy(themeMode = themeMode) }
    }

    fun isIntroSeen(): Boolean {
        return preferences.getBoolean(KEY_INTRO_SEEN, false)
    }

    fun setIntroSeen(seen: Boolean) {
        preferences.edit()
            .putBoolean(KEY_INTRO_SEEN, seen)
            .apply()
    }

    private companion object {
        const val PREFERENCES_NAME = "predicta_settings"
        const val KEY_THEME_MODE = "theme_mode"
        const val KEY_INTRO_SEEN = "intro_seen"
    }
}
