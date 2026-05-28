package com.predicta.app.feature_tasks.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.predicta.app.core.error.AppResult
import com.predicta.app.core.ui.toUiText
import com.predicta.app.data.remote.PredictaApi
import com.predicta.app.data.remote.dto.CreateTaskRequest
import com.predicta.app.feature_employees.domain.repository.EmployeeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TaskViewModel(
    private val employeeRepository: EmployeeRepository,
    private val api: PredictaApi,
) : ViewModel() {

    private val _state = MutableStateFlow(TaskState())
    val state: StateFlow<TaskState> = _state.asStateFlow()

    init {
        loadEmployees()
    }

    fun onEvent(event: TaskEvent) {
        when (event) {
            is TaskEvent.UpdateTitle -> {
                _state.update { it.copy(taskTitle = event.text) }
            }
            is TaskEvent.UpdateDescription -> {
                _state.update { it.copy(taskDescription = event.text) }
            }
            is TaskEvent.SelectEmployee -> {
                _state.update {
                    it.copy(
                        selectedEmployee = event.employee,
                        isDropdownExpanded = false,
                        aiInsight = null,
                        suggestedEmployee = null
                    )
                }
            }
            is TaskEvent.ToggleDropdown -> {
                _state.update { it.copy(isDropdownExpanded = !it.isDropdownExpanded) }
            }
            is TaskEvent.DismissDropdown -> {
                _state.update { it.copy(isDropdownExpanded = false) }
            }
            TaskEvent.SubmitTask -> submitTask(force = false)
            TaskEvent.ForceSubmitTask -> submitTask(force = true)
            TaskEvent.SelectSuggestedEmployee -> {
                val suggested = _state.value.suggestedEmployee
                if (suggested != null) {
                    _state.update {
                        it.copy(
                            selectedEmployee = suggested,
                            suggestedEmployee = null,
                            aiInsight = null
                        )
                    }
                }
            }
            TaskEvent.ResetSuccessState -> {
                _state.update { it.copy(isSuccess = false) }
            }
        }
    }

    private fun loadEmployees() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (val result = employeeRepository.getTeamVelocity()) {
                is AppResult.Success -> {
                    val members = result.value
                    _state.update {
                        it.copy(
                            isLoading = false,
                            employees = members,
                            selectedEmployee = members.firstOrNull()
                        )
                    }
                }
                is AppResult.Failure -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = result.error.toUiText(),
                        )
                    }
                }
            }
        }
    }

    private fun submitTask(force: Boolean) {
        val currentState = _state.value
        val assignee = currentState.selectedEmployee
        if (assignee == null) {
            _state.update { it.copy(error = "Выберите исполнителя") }
            return
        }
        if (currentState.taskTitle.isBlank()) {
            _state.update { it.copy(error = "Заполните название задачи") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val response = api.createTask(
                    CreateTaskRequest(
                        title = currentState.taskTitle,
                        description = currentState.taskDescription,
                        assigneeId = assignee.id,
                        force = force
                    )
                )
                
                if (response.created) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true,
                            aiInsight = null,
                            suggestedEmployee = null
                        )
                    }
                } else {
                    val suggestedMember = if (response.suggestedAssigneeId != null) {
                        currentState.employees.find { it.id == response.suggestedAssigneeId }
                    } else null
                    
                    _state.update {
                        it.copy(
                            isLoading = false,
                            aiInsight = response.aiInsight,
                            suggestedEmployee = suggestedMember
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Ошибка сети: ${e.message}"
                    )
                }
            }
        }
    }
}
