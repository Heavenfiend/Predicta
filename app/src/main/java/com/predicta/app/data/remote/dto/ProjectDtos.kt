package com.predicta.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProjectStatusResponse(
    @SerialName("sprint_name") val sprintName: String,
    @SerialName("completion_pct") val completionPct: Double,
    @SerialName("delay_days") val delayDays: Int,
    @SerialName("is_at_risk") val isAtRisk: Boolean,
    @SerialName("risk_message") val riskMessage: String = "",
    @SerialName("ai_advice") val aiAdvice: String = "",
    @SerialName("track_name") val trackName: String = "",
    @SerialName("days_remaining") val daysRemaining: Int = 0,
)
