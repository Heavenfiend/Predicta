package com.predicta.app.feature_dashboard.domain.model

data class ProjectStatus(
    val sprintName: String,
    val completionPct: Double,
    val delayDays: Int,
    val isAtRisk: Boolean,
    val riskMessage: String,
    val aiAdvice: String,
    val trackName: String,
    val daysRemaining: Int,
)
