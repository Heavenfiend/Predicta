package com.predicta.app.feature_dashboard.presentation

sealed interface DashboardEvent {
    data object Refresh : DashboardEvent
    data object NavigateToTeamVelocity : DashboardEvent
}
