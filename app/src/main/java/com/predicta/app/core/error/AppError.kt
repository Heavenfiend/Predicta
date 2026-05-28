package com.predicta.app.core.error

sealed interface AppError {
    data object Network : AppError
    data class Validation(
        val field: ValidationField,
        val reason: ValidationReason,
    ) : AppError
    data object Auth : AppError
    data class Unknown(val technicalMessage: String? = null) : AppError
}

enum class ValidationField {
    EMAIL,
    PASSWORD,
    NAME,
    RECOVERY_CODE,
    CONFIRM_PASSWORD,
    FIRST_NAME,
    LAST_NAME,
    TELEGRAM_NICK,
    PHONE,
}

enum class ValidationReason {
    BLANK,
    INVALID_EMAIL,
    TOO_SHORT,
    MISMATCH,
}
