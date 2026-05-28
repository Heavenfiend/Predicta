package com.predicta.app.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class HealthResponse(
    val status: String,
)
