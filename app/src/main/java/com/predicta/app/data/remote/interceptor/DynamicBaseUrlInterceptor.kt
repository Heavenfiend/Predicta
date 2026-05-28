package com.predicta.app.data.remote.interceptor

import com.predicta.app.feature_settings.data.repository.AppSettingsRepository
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Dynamically rewrites the scheme, host, and port of outgoing API requests
 * using the server URL configured in settings, enabling run-time backend changes (e.g. ngrok restarts).
 */
class DynamicBaseUrlInterceptor(
    private val settingsRepository: AppSettingsRepository,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val customUrlString = settingsRepository.settings.value.baseUrl.trim()

        if (customUrlString.isNotBlank() && !customUrlString.contains("xxxx", ignoreCase = true)) {
            val customUrl = customUrlString.toHttpUrlOrNull()
            if (customUrl != null) {
                val newUrl = originalRequest.url.newBuilder()
                    .scheme(customUrl.scheme)
                    .host(customUrl.host)
                    .port(customUrl.port)
                    .build()

                val newRequest = originalRequest.newBuilder()
                    .url(newUrl)
                    .build()

                return chain.proceed(newRequest)
            }
        }

        return chain.proceed(originalRequest)
    }
}
