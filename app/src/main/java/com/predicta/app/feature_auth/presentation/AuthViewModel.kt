package com.predicta.app.feature_auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.predicta.app.core.error.AppResult
import com.predicta.app.core.ui.UiEffect
import com.predicta.app.core.ui.toUiText
import com.predicta.app.feature_auth.data.session.UserSessionManager
import com.predicta.app.feature_auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository,
    private val sessionManager: UserSessionManager,
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<AuthEffect>()
    val effects: SharedFlow<AuthEffect> = _effects.asSharedFlow()

    fun onEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.EmailChanged,
            is AuthEvent.PasswordChanged,
            is AuthEvent.NameChanged,
            is AuthEvent.FirstNameChanged,
            is AuthEvent.LastNameChanged,
            is AuthEvent.TelegramNickChanged,
            is AuthEvent.PhoneChanged,
            AuthEvent.FillDemoCredentials,
            is AuthEvent.RecoveryCodeChanged,
            is AuthEvent.NewPasswordChanged,
            is AuthEvent.ConfirmPasswordChanged,
            AuthEvent.ResetSuccessState,
            AuthEvent.ResetPasswordRecoveryStep,
            -> _state.update { reduceAuthInput(it, event) }

            AuthEvent.LoginDemoSubmit -> {
                _state.update { reduceAuthInput(it, event) }
                login()
            }
            AuthEvent.LoginSubmit -> login()
            AuthEvent.RegisterSubmit -> register()
            AuthEvent.ResetSubmit,
            AuthEvent.SubmitEmailForReset,
            AuthEvent.SubmitRecoveryCode,
            AuthEvent.SubmitNewPasswords,
            -> { /* Password reset not supported by API */ }
        }
    }

    private fun login() {
        val errors = validateLogin(_state.value)
        if (errors.isNotEmpty()) {
            _state.update { applyValidationErrors(it.copy(globalError = null), errors) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, globalError = null) }
            when (val result = repository.login(_state.value.email, _state.value.password)) {
                is AppResult.Success -> {
                    sessionManager.startSession(result.value)
                    _state.update { it.copy(isLoading = false, isSuccess = true) }
                    _effects.emit(AuthEffect.Authenticated)
                }
                is AppResult.Failure -> {
                    _state.update {
                        it.copy(isLoading = false, globalError = result.error.toUiText())
                    }
                }
            }
        }
    }

    private fun register() {
        val errors = validateRegister(_state.value)
        if (errors.isNotEmpty()) {
            _state.update { applyValidationErrors(it.copy(globalError = null), errors) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, globalError = null) }
            val result = repository.register(
                firstName = _state.value.firstName,
                lastName = _state.value.lastName,
                email = _state.value.email,
                password = _state.value.password,
                telegramNick = _state.value.telegramNick,
                phone = _state.value.phone,
                avatarUrl = null
            )
            when (result) {
                is AppResult.Success -> {
                    _state.update { it.copy(isLoading = false, isSuccess = true) }
                }
                is AppResult.Failure -> {
                    _state.update {
                        it.copy(isLoading = false, globalError = result.error.toUiText())
                    }
                }
            }
        }
    }
}

sealed interface AuthEffect : UiEffect {
    data object Authenticated : AuthEffect
}
