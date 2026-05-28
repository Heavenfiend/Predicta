package com.predicta.app.feature_dashboard.domain.repository

import com.predicta.app.core.error.AppResult
import com.predicta.app.feature_dashboard.domain.model.ProjectStatus

interface DashboardRepository {
    suspend fun getProjectStatus(): AppResult<ProjectStatus>
    suspend fun getTeamInsights(): AppResult<String>
}
