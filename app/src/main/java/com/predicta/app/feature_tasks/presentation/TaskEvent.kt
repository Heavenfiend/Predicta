package com.predicta.app.feature_tasks.presentation

import com.predicta.app.feature_employees.domain.model.TeamMember

sealed interface TaskEvent {
    data class UpdateTitle(val text: String) : TaskEvent
    data class UpdateDescription(val text: String) : TaskEvent
    data class SelectEmployee(val employee: TeamMember) : TaskEvent
    data object ToggleDropdown : TaskEvent
    data object DismissDropdown : TaskEvent
    data object SubmitTask : TaskEvent
    data object ForceSubmitTask : TaskEvent
    data object SelectSuggestedEmployee : TaskEvent
    data object ResetSuccessState : TaskEvent
}
