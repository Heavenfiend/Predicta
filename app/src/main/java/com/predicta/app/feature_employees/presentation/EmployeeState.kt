package com.predicta.app.feature_employees.presentation

import com.predicta.app.feature_employees.domain.model.TeamMember

data class EmployeeState(
    val isLoading: Boolean = true,
    val teamMembers: List<TeamMember> = emptyList(),
    val teamInsight: String = "",
    val error: String? = null,
)
