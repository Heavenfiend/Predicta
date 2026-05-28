package com.predicta.app.feature_employees.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.predicta.app.core.error.AppResult
import com.predicta.app.core.ui.toUiText
import com.predicta.app.feature_employees.domain.repository.EmployeeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EmployeeCardViewModel(
    savedStateHandle: SavedStateHandle,
    private val employeeRepository: EmployeeRepository,
) : ViewModel() {

    private val employeeId: String = checkNotNull(savedStateHandle["employeeId"])

    private val _state = MutableStateFlow(EmployeeCardState())
    val state: StateFlow<EmployeeCardState> = _state.asStateFlow()

    init {
        loadEmployeeData()
    }

    private fun loadEmployeeData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            // Load employee detail
            when (val result = employeeRepository.getEmployeeDetail(employeeId)) {
                is AppResult.Success -> {
                    val detail = result.value
                    _state.update {
                        it.copy(
                            isLoading = false,
                            employeeId = detail.id,
                            name = detail.name,
                            role = detail.role,
                            telegramNick = detail.telegramNick,
                            avatarUrl = detail.avatarUrl,
                            doneCount = detail.doneCount,
                            totalCount = detail.totalCount,
                            remainingCount = detail.remainingCount,
                            health = detail.health,
                            aiInsight = detail.aiInsight,
                            tasks = detail.tasks,
                        )
                    }
                }
                is AppResult.Failure -> {
                    _state.update {
                        it.copy(isLoading = false, error = result.error.toUiText())
                    }
                }
            }

            // Load analytics (non-blocking)
            when (val analyticsResult = employeeRepository.getEmployeeAnalytics(employeeId)) {
                is AppResult.Success -> {
                    val analytics = analyticsResult.value
                    _state.update {
                        it.copy(
                            forecastDaysToComplete = analytics.forecastDaysToComplete,
                            sprintDaysLeft = analytics.sprintDaysLeft,
                            delayDays = analytics.delayDays,
                            analyticsAiInsight = analytics.aiInsight,
                        )
                    }
                }
                is AppResult.Failure -> { /* Silently ignore analytics failure */ }
            }
        }
    }
}
