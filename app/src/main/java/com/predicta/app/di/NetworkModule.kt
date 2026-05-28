package com.predicta.app.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.predicta.app.data.remote.PredictaApi
import com.predicta.app.data.remote.interceptor.AuthInterceptor
import com.predicta.app.data.remote.interceptor.DynamicBaseUrlInterceptor
import com.predicta.app.data.remote.interceptor.NgrokInterceptor
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import com.predicta.app.core.network.NetworkConfig
import org.koin.dsl.module
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

val networkModule = module {

    single {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            encodeDefaults = true
            prettyPrint = false
            coerceInputValues = true
        }
    }

    single {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .addInterceptor(DynamicBaseUrlInterceptor(settingsRepository = get()))
            .addInterceptor(NgrokInterceptor())
            .addInterceptor(AuthInterceptor(sessionManager = get()))
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    single<Retrofit> {
        val json: Json = get()
        val contentType = "application/json".toMediaType()

        Retrofit.Builder()
            .baseUrl(NetworkConfig.BASE_URL)
            .client(get())
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    single<PredictaApi> {
        get<Retrofit>().create(PredictaApi::class.java)
    }
}
