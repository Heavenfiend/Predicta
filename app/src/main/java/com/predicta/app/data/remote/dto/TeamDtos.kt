package com.predicta.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TeamMemberResponse(
    val id: String,
    val name: String,
    val role: String,
    @SerialName("done_count") val doneCount: Int,
    @SerialName("total_count") val totalCount: Int,
    val health: String, // "good" | "normal" | "bad"
)

@Serializable
data class TeamInsightsResponse(
    @SerialName("ai_insight") val aiInsight: String,
)
