package com.predicta.app.core.ui

import com.predicta.app.core.error.AppError
import com.predicta.app.core.error.ValidationField
import com.predicta.app.core.error.ValidationReason

fun AppError.toUiText(): String {
    return when (this) {
        AppError.Auth -> "Неверные учетные данные"
        AppError.Network -> "Не удалось подключиться к серверу"
        is AppError.Unknown -> "Что-то пошло не так. Попробуйте еще раз"
        is AppError.Validation -> toUiText()
    }
}

private fun AppError.Validation.toUiText(): String {
    return when (field) {
        ValidationField.EMAIL -> when (reason) {
            ValidationReason.BLANK -> "Электронная почта не может быть пустой"
            ValidationReason.INVALID_EMAIL -> "Неверный формат электронной почты"
            else -> "Проверьте электронную почту"
        }
        ValidationField.PASSWORD,
        ValidationField.CONFIRM_PASSWORD,
        -> when (reason) {
            ValidationReason.BLANK -> "Пароль не может быть пустым"
            ValidationReason.TOO_SHORT -> "Минимум $MIN_PASSWORD_LENGTH символов"
            ValidationReason.MISMATCH -> "Пароли не совпадают"
            else -> "Проверьте пароль"
        }
        ValidationField.NAME -> "Имя не может быть пустым"
        ValidationField.RECOVERY_CODE -> "Код не может быть пустым"
        ValidationField.FIRST_NAME -> "Имя не может быть пустым"
        ValidationField.LAST_NAME -> "Фамилия не может быть пустой"
        ValidationField.TELEGRAM_NICK -> "Telegram не может быть пустым"
        ValidationField.PHONE -> "Телефон не может быть пустым"
    }
}

private const val MIN_PASSWORD_LENGTH = 5
