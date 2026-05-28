package com.predicta.app.feature_employees.presentation

import com.predicta.app.feature_employees.domain.model.EmployeeTask
import com.predicta.app.feature_employees.domain.model.Health

data class EmployeeCardState(
    val isLoading: Boolean = true,
    val employeeId: String = "",
    val name: String = "",
    val role: String = "",
    val telegramNick: String = "",
    val avatarUrl: String? = null,
    val doneCount: Int = 0,
    val totalCount: Int = 0,
    val remainingCount: Int = 0,
    val health: Health = Health.NORMAL,
    val aiInsight: String = "",
    val tasks: List<EmployeeTask> = emptyList(),
    // Analytics
    val forecastDaysToComplete: Int = 0,
    val sprintDaysLeft: Int = 0,
    val delayDays: Int = 0,
    val analyticsAiInsight: String = "",
    val error: String? = null,
)
