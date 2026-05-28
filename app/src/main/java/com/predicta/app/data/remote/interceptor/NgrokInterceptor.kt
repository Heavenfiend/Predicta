package com.predicta.app.data.remote.interceptor

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Adds the `ngrok-skip-browser-warning: true` header to every request
 * to bypass the ngrok browser interstitial page.
 */
class NgrokInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("ngrok-skip-browser-warning", "true")
            .build()
        return chain.proceed(request)
    }
}
