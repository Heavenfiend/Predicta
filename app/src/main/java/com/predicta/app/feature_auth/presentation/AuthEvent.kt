package com.predicta.app.feature_auth.presentation

sealed interface AuthEvent {
    data class EmailChanged(val value: String) : AuthEvent
    data class PasswordChanged(val value: String) : AuthEvent
    data class NameChanged(val value: String) : AuthEvent
    data class FirstNameChanged(val value: String) : AuthEvent
    data class LastNameChanged(val value: String) : AuthEvent
    data class TelegramNickChanged(val value: String) : AuthEvent
    data class PhoneChanged(val value: String) : AuthEvent
    data object FillDemoCredentials : AuthEvent
    data object LoginSubmit : AuthEvent
    data object LoginDemoSubmit : AuthEvent
    data object RegisterSubmit : AuthEvent
    data object ResetSubmit : AuthEvent
    data object ResetSuccessState : AuthEvent

    // Multi-step password recovery events
    data class RecoveryCodeChanged(val value: String) : AuthEvent
    data class NewPasswordChanged(val value: String) : AuthEvent
    data class ConfirmPasswordChanged(val value: String) : AuthEvent
    data object SubmitEmailForReset : AuthEvent
    data object SubmitRecoveryCode : AuthEvent
    data object SubmitNewPasswords : AuthEvent
    data object ResetPasswordRecoveryStep : AuthEvent
}
