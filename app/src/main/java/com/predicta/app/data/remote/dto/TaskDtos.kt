package com.predicta.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ── Create Task ─────────────────────────────────────────────────────────────

@Serializable
data class CreateTaskRequest(
    val title: String,
    val description: String? = null,
    @SerialName("assignee_id") val assigneeId: String,
    val force: Boolean = false,
)

@Serializable
data class CreateTaskResponse(
    val created: Boolean,
    val approved: Boolean,
    @SerialName("task_id") val taskId: String? = null,
    @SerialName("task_title") val taskTitle: String? = null,
    @SerialName("assignee_id") val assigneeId: String,
    @SerialName("assignee_name") val assigneeName: String,
    @SerialName("suggested_assignee_id") val suggestedAssigneeId: String? = null,
    @SerialName("suggested_assignee_name") val suggestedAssigneeName: String? = null,
    @SerialName("ai_insight") val aiInsight: String = "",
)

// ── Reassign Task ───────────────────────────────────────────────────────────

@Serializable
data class ReassignTaskRequest(
    @SerialName("task_id") val taskId: String,
    @SerialName("new_executor_id") val newExecutorId: String,
)

@Serializable
data class ReassignTaskResponse(
    val message: String,
    @SerialName("project_status") val projectStatus: ProjectStatusResponse? = null,
)

// ── Error ───────────────────────────────────────────────────────────────────

@Serializable
data class ErrorResponse(
    val error: String,
)
