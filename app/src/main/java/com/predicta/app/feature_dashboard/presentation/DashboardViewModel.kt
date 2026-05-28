package com.predicta.app.feature_dashboard.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.predicta.app.core.error.AppResult
import com.predicta.app.core.ui.UiEffect
import com.predicta.app.core.ui.toUiText
import com.predicta.app.feature_dashboard.domain.repository.DashboardRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val repository: DashboardRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<DashboardEffect>()
    val effects: SharedFlow<DashboardEffect> = _effects.asSharedFlow()

    init {
        loadData()
    }

    fun onEvent(event: DashboardEvent) {
        when (event) {
            DashboardEvent.Refresh -> loadData()
            DashboardEvent.NavigateToTeamVelocity -> {
                viewModelScope.launch {
                    _effects.emit(DashboardEffect.GoToTeamVelocity)
                }
            }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            // Load project status
            when (val result = repository.getProjectStatus()) {
                is AppResult.Success -> {
                    val status = result.value
                    _state.update {
                        it.copy(
                            isLoading = false,
                            sprintName = status.sprintName,
                            completionPct = status.completionPct,
                            delayDays = status.delayDays,
                            isAtRisk = status.isAtRisk,
                            riskMessage = status.riskMessage,
                            aiAdvice = status.aiAdvice,
                            trackName = status.trackName,
                            daysRemaining = status.daysRemaining,
                        )
                    }
                }
                is AppResult.Failure -> {
                    _state.update {
                        it.copy(isLoading = false, error = result.error.toUiText())
                    }
                }
            }

            // Load team insights (non-blocking — if it fails, just skip)
            when (val insightResult = repository.getTeamInsights()) {
                is AppResult.Success -> {
                    _state.update { it.copy(teamInsight = insightResult.value) }
                }
                is AppResult.Failure -> { /* Silently ignore */ }
            }
        }
    }
}

sealed interface DashboardEffect : UiEffect {
    data object GoToTeamVelocity : DashboardEffect
}
