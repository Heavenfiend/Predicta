package com.predicta.app.data.remote.interceptor

import com.predicta.app.feature_auth.data.session.UserSessionManager
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Adds the `Authorization: Bearer <token>` header to all api requests,
 * except for /api/auth/register and /api/auth/login which are public.
 */
class AuthInterceptor(
    private val sessionManager: UserSessionManager,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val path = original.url.encodedPath

        // Only add auth header for /api/* paths, excluding register and login
        val needsAuth = path.startsWith("/api/") &&
            !path.endsWith("/auth/register") &&
            !path.endsWith("/auth/login")

        val request = if (needsAuth) {
            val token = sessionManager.getToken()
            if (token != null) {
                original.newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
            } else {
                original
            }
        } else {
            original
        }

        return chain.proceed(request)
    }
}
