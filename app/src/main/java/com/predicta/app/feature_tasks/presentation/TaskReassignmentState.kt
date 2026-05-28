package com.predicta.app.feature_tasks.presentation

import com.predicta.app.feature_employees.domain.model.TeamMember

data class TaskReassignmentState(
    val isLoading: Boolean = true,
    val taskId: String = "",
    val taskTitle: String = "",
    val fromName: String = "",
    val fromId: String = "",
    val toExecutors: List<TeamMember> = emptyList(),
    val selectedExecutorId: String? = null,
    val canReassign: Boolean = false,
    val isReassigned: Boolean = false,
    val error: String? = null,
)
