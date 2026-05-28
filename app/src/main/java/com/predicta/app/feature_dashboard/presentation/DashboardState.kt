package com.predicta.app.feature_dashboard.presentation

data class DashboardState(
    val isLoading: Boolean = true,
    val sprintName: String = "",
    val completionPct: Double = 0.0,
    val delayDays: Int = 0,
    val isAtRisk: Boolean = false,
    val riskMessage: String = "",
    val aiAdvice: String = "",
    val trackName: String = "",
    val daysRemaining: Int = 0,
    val teamInsight: String = "",
    val error: String? = null,
)
