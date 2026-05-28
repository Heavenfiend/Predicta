package com.predicta.app.feature_employees.domain.repository

import com.predicta.app.core.error.AppResult
import com.predicta.app.feature_employees.domain.model.EmployeeAnalytics
import com.predicta.app.feature_employees.domain.model.EmployeeDetail
import com.predicta.app.feature_employees.domain.model.TeamMember

interface EmployeeRepository {
    suspend fun getTeamVelocity(): AppResult<List<TeamMember>>
    suspend fun getEmployeeDetail(id: String): AppResult<EmployeeDetail>
    suspend fun getEmployeeAnalytics(id: String): AppResult<EmployeeAnalytics>
}
