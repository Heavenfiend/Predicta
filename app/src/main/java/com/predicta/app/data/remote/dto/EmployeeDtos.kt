package com.predicta.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EmployeeDetailResponse(
    val id: String,
    val name: String,
    val role: String,
    @SerialName("telegram_nick") val telegramNick: String = "",
    @SerialName("avatar_url") val avatarUrl: String? = null,
    @SerialName("done_count") val doneCount: Int,
    @SerialName("total_count") val totalCount: Int,
    @SerialName("remaining_count") val remainingCount: Int,
    val health: String, // "good" | "normal" | "bad"
    @SerialName("ai_insight") val aiInsight: String = "",
    val tasks: List<TaskDto> = emptyList(),
)

@Serializable
data class EmployeeAnalyticsResponse(
    @SerialName("employee_id") val employeeId: String,
    @SerialName("employee_name") val employeeName: String,
    val role: String,
    @SerialName("forecast_days_to_complete") val forecastDaysToComplete: Int,
    @SerialName("sprint_days_left") val sprintDaysLeft: Int,
    @SerialName("delay_days") val delayDays: Int,
    @SerialName("ai_insight") val aiInsight: String = "",
    val tasks: List<TaskDto> = emptyList(),
)

@Serializable
data class TaskDto(
    val id: String,
    val title: String,
    val status: String, // "todo" | "in_progress" | "done"
)
