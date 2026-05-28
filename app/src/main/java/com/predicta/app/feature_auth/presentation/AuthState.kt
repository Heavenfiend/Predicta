package com.predicta.app.feature_auth.presentation

enum class ResetStep {
    EMAIL_INPUT,
    CODE_VERIFICATION,
    NEW_PASSWORD,
    SUCCESS
}

data class AuthState(
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val name: String = "",
    val nameError: String? = null,
    val firstName: String = "",
    val firstNameError: String? = null,
    val lastName: String = "",
    val lastNameError: String? = null,
    val telegramNick: String = "",
    val telegramNickError: String? = null,
    val phone: String = "",
    val phoneError: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val globalError: String? = null,

    // Multi-step password recovery fields
    val resetStep: ResetStep = ResetStep.EMAIL_INPUT,
    val recoveryCode: String = "",
    val recoveryCodeError: String? = null,
    val newPassword: String = "",
    val newPasswordError: String? = null,
    val confirmPassword: String = "",
    val confirmPasswordError: String? = null,
)
