package com.predicta.app.feature_settings.domain.model

enum class ThemeMode(
    val storageValue: String,
    val title: String,
    val description: String,
) {
    SYSTEM(
        storageValue = "system",
        title = "Как в системе",
        description = "Автоматически повторяет тему устройства",
    ),
    LIGHT(
        storageValue = "light",
        title = "Светлая",
        description = "Всегда использовать светлый интерфейс",
    ),
    DARK(
        storageValue = "dark",
        title = "Темная",
        description = "Всегда использовать темный интерфейс",
    );

    companion object {
        fun fromStorageValue(value: String?): ThemeMode {
            return entries.firstOrNull { it.storageValue == value } ?: SYSTEM
        }
    }
}

data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val baseUrl: String = "",
)
