package com.predicta.app.feature_settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.predicta.app.feature_auth.data.session.UserSessionManager
import com.predicta.app.feature_settings.data.repository.AppSettingsRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: AppSettingsRepository,
    private val sessionManager: UserSessionManager,
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                settingsRepository.settings,
                sessionManager.session,
            ) { appSettings, session ->
                SettingsState(
                    themeMode = appSettings.themeMode,
                    userName = session.userName,
                    email = session.email,
                    role = session.role,
                    isLoggedIn = session.isLoggedIn,
                    avatarUri = session.avatarUri,
                    baseUrl = appSettings.baseUrl,
                )
            }.collect { settingsState ->
                _state.update {
                    settingsState
                }
            }
        }
    }

    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.ChangeTheme -> {
                settingsRepository.setThemeMode(event.themeMode)
            }
            is SettingsEvent.UpdateName -> {
                sessionManager.updateName(event.name)
            }
            is SettingsEvent.UpdateAvatar -> {
                sessionManager.updateAvatar(event.uri)
            }
            is SettingsEvent.UpdateBaseUrl -> {
                settingsRepository.setBaseUrl(event.url)
            }
            SettingsEvent.Logout -> {
                sessionManager.clearSession()
            }
        }
    }
}
