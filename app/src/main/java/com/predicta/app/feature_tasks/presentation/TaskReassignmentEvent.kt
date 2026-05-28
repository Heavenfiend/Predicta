package com.predicta.app.feature_tasks.presentation

sealed interface TaskReassignmentEvent {
    data class SelectExecutor(val executorId: String) : TaskReassignmentEvent
    data object ConfirmReassignment : TaskReassignmentEvent
    data object CompleteReassignment : TaskReassignmentEvent
}
