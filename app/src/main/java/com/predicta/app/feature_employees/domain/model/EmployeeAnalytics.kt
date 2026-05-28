package com.predicta.app.feature_employees.domain.model

data class EmployeeAnalytics(
    val employeeId: String,
    val employeeName: String,
    val role: String,
    val forecastDaysToComplete: Int,
    val sprintDaysLeft: Int,
    val delayDays: Int,
    val aiInsight: String,
    val tasks: List<EmployeeTask>,
)
