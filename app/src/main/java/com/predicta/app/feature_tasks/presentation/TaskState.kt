package com.predicta.app.feature_tasks.presentation

import com.predicta.app.feature_employees.domain.model.TeamMember

data class TaskState(
    val isLoading: Boolean = true,
    val taskTitle: String = "",
    val taskDescription: String = "",
    val employees: List<TeamMember> = emptyList(),
    val selectedEmployee: TeamMember? = null,
    val isDropdownExpanded: Boolean = false,
    val aiInsight: String? = null,
    val suggestedEmployee: TeamMember? = null,
    val isSuccess: Boolean = false,
    val error: String? = null,
)
