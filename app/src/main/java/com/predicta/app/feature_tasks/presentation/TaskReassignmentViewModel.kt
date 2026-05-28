package com.predicta.app.feature_tasks.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.predicta.app.core.error.AppResult
import com.predicta.app.core.ui.UiEffect
import com.predicta.app.core.ui.toUiText
import com.predicta.app.feature_employees.domain.repository.EmployeeRepository
import com.predicta.app.feature_tasks.domain.usecase.ReassignTaskUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TaskReassignmentViewModel(
    savedStateHandle: SavedStateHandle,
    private val employeeRepository: EmployeeRepository,
    private val reassignTaskUseCase: ReassignTaskUseCase,
) : ViewModel() {

    private val taskId: String = checkNotNull(savedStateHandle["taskId"])

    private val _state = MutableStateFlow(TaskReassignmentState())
    val state: StateFlow<TaskReassignmentState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<TaskReassignmentEffect>()
    val effects: SharedFlow<TaskReassignmentEffect> = _effects.asSharedFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            // 1. Fetch team velocity
            when (val teamResult = employeeRepository.getTeamVelocity()) {
                is AppResult.Success -> {
                    val members = teamResult.value
                    var foundTaskId: String? = null
                    var foundTaskTitle: String? = null
                    var foundFromName: String? = null
                    var foundFromId: String? = null
                    
                    // 2. Scan each member details to find the task
                    for (member in members) {
                        when (val detailResult = employeeRepository.getEmployeeDetail(member.id)) {
                            is AppResult.Success -> {
                                val detail = detailResult.value
                                val task = detail.tasks.find { it.id == taskId }
                                if (task != null) {
                                    foundTaskId = task.id
                                    foundTaskTitle = task.title
                                    foundFromName = detail.name
                                    foundFromId = detail.id
                                    break
                                }
                            }
                            is AppResult.Failure -> { /* Ignore error for individual member scan */ }
                        }
                    }
                    
                    if (foundTaskId != null && foundFromName != null && foundFromId != null) {
                        val otherMembers = members.filter { it.id != foundFromId }
                        _state.update {
                            it.copy(
                                isLoading = false,
                                taskId = foundTaskId,
                                taskTitle = foundTaskTitle ?: "",
                                fromName = foundFromName,
                                fromId = foundFromId,
                                toExecutors = otherMembers,
                                selectedExecutorId = otherMembers.firstOrNull()?.id,
                                canReassign = true
                            )
                        }
                    } else {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                canReassign = false,
                                error = "Задача не найдена или не назначена сотруднику"
                            )
                        }
                    }
                }
                is AppResult.Failure -> {
                    _state.update {
                        it.copy(isLoading = false, error = teamResult.error.toUiText())
                    }
                }
            }
        }
    }

    fun onEvent(event: TaskReassignmentEvent) {
        when (event) {
            is TaskReassignmentEvent.SelectExecutor -> {
                _state.update { it.copy(selectedExecutorId = event.executorId) }
            }
            is TaskReassignmentEvent.ConfirmReassignment -> {
                confirmReassignment()
            }
            is TaskReassignmentEvent.CompleteReassignment -> {
                viewModelScope.launch {
                    _effects.emit(TaskReassignmentEffect.GoToDashboard)
                }
            }
        }
    }

    private fun confirmReassignment() {
        val selectedId = _state.value.selectedExecutorId
        if (selectedId == null) {
            _state.update { it.copy(error = "Выберите исполнителя") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (val result = reassignTaskUseCase(taskId = taskId, newExecutorId = selectedId)) {
                is AppResult.Success -> {
                    _state.update { it.copy(isLoading = false, isReassigned = true) }
                }
                is AppResult.Failure -> {
                    _state.update {
                        it.copy(isLoading = false, error = result.error.toUiText())
                    }
                }
            }
        }
    }
}

sealed interface TaskReassignmentEffect : UiEffect {
    data object GoToDashboard : TaskReassignmentEffect
}
