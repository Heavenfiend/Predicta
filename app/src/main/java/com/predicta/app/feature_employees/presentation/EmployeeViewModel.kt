package com.predicta.app.feature_employees.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.predicta.app.core.error.AppResult
import com.predicta.app.core.ui.UiEffect
import com.predicta.app.core.ui.toUiText
import com.predicta.app.feature_dashboard.domain.repository.DashboardRepository
import com.predicta.app.feature_employees.domain.repository.EmployeeRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EmployeeViewModel(
    private val employeeRepository: EmployeeRepository,
    private val dashboardRepository: DashboardRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(EmployeeState())
    val state: StateFlow<EmployeeState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<EmployeeEffect>()
    val effects: SharedFlow<EmployeeEffect> = _effects.asSharedFlow()

    init {
        loadTeamVelocity()
    }

    fun onEvent(event: EmployeeEvent) {
        when (event) {
            is EmployeeEvent.Refresh -> loadTeamVelocity()
            is EmployeeEvent.SelectEmployee -> {
                viewModelScope.launch {
                    _effects.emit(EmployeeEffect.GoToEmployeeCard(event.employeeId))
                }
            }
        }
    }

    private fun loadTeamVelocity() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            when (val result = employeeRepository.getTeamVelocity()) {
                is AppResult.Success -> {
                    _state.update {
                        it.copy(isLoading = false, teamMembers = result.value)
                    }
                }
                is AppResult.Failure -> {
                    _state.update {
                        it.copy(isLoading = false, error = result.error.toUiText())
                    }
                }
            }

            // Load team insights (non-blocking)
            when (val insightResult = dashboardRepository.getTeamInsights()) {
                is AppResult.Success -> {
                    _state.update { it.copy(teamInsight = insightResult.value) }
                }
                is AppResult.Failure -> { /* Silently ignore */ }
            }
        }
    }
}

sealed interface EmployeeEffect : UiEffect {
    data class GoToEmployeeCard(val employeeId: String) : EmployeeEffect
}
