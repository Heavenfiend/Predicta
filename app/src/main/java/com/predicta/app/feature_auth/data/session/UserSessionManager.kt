package com.predicta.app.feature_auth.data.session

import android.content.Context
import com.predicta.app.feature_auth.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class UserSessionManager(context: Context) {

    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    private val _session = MutableStateFlow(
        UserSession(
            isLoggedIn = preferences.getBoolean(KEY_IS_LOGGED_IN, false),
            userName = preferences.getString(KEY_USER_NAME, "").orEmpty(),
            email = preferences.getString(KEY_EMAIL, "").orEmpty(),
            role = preferences.getString(KEY_ROLE, "").orEmpty(),
            avatarUri = preferences.getString(KEY_AVATAR_URI, null),
            token = preferences.getString(KEY_TOKEN, null),
        ),
    )
    val session: StateFlow<UserSession> = _session.asStateFlow()

    fun getToken(): String? = _session.value.token

    fun startSession(user: User) {
        preferences.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putString(KEY_USER_NAME, user.name)
            .putString(KEY_EMAIL, user.email)
            .putString(KEY_ROLE, user.role)
            .putString(KEY_TOKEN, user.token)
            .putString(KEY_AVATAR_URI, user.avatarUrl)
            .apply()

        _session.update {
            it.copy(
                isLoggedIn = true,
                userName = user.name,
                email = user.email,
                role = user.role,
                token = user.token,
                avatarUri = user.avatarUrl,
            )
        }
    }

    fun clearSession() {
        preferences.edit()
            .remove(KEY_IS_LOGGED_IN)
            .remove(KEY_USER_NAME)
            .remove(KEY_EMAIL)
            .remove(KEY_ROLE)
            .remove(KEY_TOKEN)
            .remove(KEY_AVATAR_URI)
            .apply()

        _session.value = UserSession()
    }

    fun updateName(name: String) {
        preferences.edit().putString(KEY_USER_NAME, name).apply()
        _session.update { it.copy(userName = name) }
    }

    fun updateAvatar(uri: String) {
        preferences.edit().putString(KEY_AVATAR_URI, uri).apply()
        _session.update { it.copy(avatarUri = uri) }
    }

    private companion object {
        const val PREFERENCES_NAME = "predicta_session"
        const val KEY_IS_LOGGED_IN = "is_logged_in"
        const val KEY_USER_NAME = "user_name"
        const val KEY_EMAIL = "email"
        const val KEY_ROLE = "role"
        const val KEY_TOKEN = "jwt_token"
        const val KEY_AVATAR_URI = "avatar_uri"
    }
}

data class UserSession(
    val isLoggedIn: Boolean = false,
    val userName: String = "",
    val email: String = "",
    val role: String = "",
    val avatarUri: String? = null,
    val token: String? = null,
)
