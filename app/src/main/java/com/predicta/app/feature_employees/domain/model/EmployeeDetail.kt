package com.predicta.app.feature_employees.domain.model

data class EmployeeDetail(
    val id: String,
    val name: String,
    val role: String,
    val telegramNick: String,
    val avatarUrl: String?,
    val doneCount: Int,
    val totalCount: Int,
    val remainingCount: Int,
    val health: Health,
    val aiInsight: String,
    val tasks: List<EmployeeTask>,
)

data class EmployeeTask(
    val id: String,
    val title: String,
    val status: TaskStatus,
)

enum class TaskStatus {
    TODO, IN_PROGRESS, DONE;

    companion object {
        fun fromString(value: String): TaskStatus = when (value.lowercase()) {
            "todo" -> TODO
            "in_progress", "in-progress" -> IN_PROGRESS
            "done" -> DONE
            else -> TODO
        }
    }
}
