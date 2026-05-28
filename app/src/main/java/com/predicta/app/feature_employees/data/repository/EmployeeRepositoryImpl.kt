package com.predicta.app.feature_employees.data.repository

import com.predicta.app.core.error.AppError
import com.predicta.app.core.error.AppResult
import com.predicta.app.data.remote.PredictaApi
import com.predicta.app.feature_employees.domain.model.EmployeeAnalytics
import com.predicta.app.feature_employees.domain.model.EmployeeDetail
import com.predicta.app.feature_employees.domain.model.EmployeeTask
import com.predicta.app.feature_employees.domain.model.Health
import com.predicta.app.feature_employees.domain.model.TaskStatus
import com.predicta.app.feature_employees.domain.model.TeamMember
import com.predicta.app.feature_employees.domain.repository.EmployeeRepository
import retrofit2.HttpException

class EmployeeRepositoryImpl(
    private val api: PredictaApi,
) : EmployeeRepository {

    override suspend fun getTeamVelocity(): AppResult<List<TeamMember>> {
        return try {
            val response = api.getTeamVelocity()
            AppResult.Success(
                response.map { dto ->
                    TeamMember(
                        id = dto.id,
                        name = dto.name,
                        role = dto.role,
                        doneCount = dto.doneCount,
                        totalCount = dto.totalCount,
                        health = Health.fromString(dto.health),
                    )
                }
            )
        } catch (e: HttpException) {
            when (e.code()) {
                401 -> AppResult.Failure(AppError.Auth)
                else -> AppResult.Failure(AppError.Unknown(e.message()))
            }
        } catch (e: Exception) {
            AppResult.Failure(AppError.Network)
        }
    }

    override suspend fun getEmployeeDetail(id: String): AppResult<EmployeeDetail> {
        return try {
            val dto = api.getEmployee(id)
            AppResult.Success(
                EmployeeDetail(
                    id = dto.id,
                    name = dto.name,
                    role = dto.role,
                    telegramNick = dto.telegramNick,
                    avatarUrl = dto.avatarUrl,
                    doneCount = dto.doneCount,
                    totalCount = dto.totalCount,
                    remainingCount = dto.remainingCount,
                    health = Health.fromString(dto.health),
                    aiInsight = dto.aiInsight,
                    tasks = dto.tasks.map { task ->
                        EmployeeTask(
                            id = task.id,
                            title = task.title,
                            status = TaskStatus.fromString(task.status),
                        )
                    },
                )
            )
        } catch (e: HttpException) {
            when (e.code()) {
                401 -> AppResult.Failure(AppError.Auth)
                else -> AppResult.Failure(AppError.Unknown(e.message()))
            }
        } catch (e: Exception) {
            AppResult.Failure(AppError.Network)
        }
    }

    override suspend fun getEmployeeAnalytics(id: String): AppResult<EmployeeAnalytics> {
        return try {
            val dto = api.getEmployeeAnalytics(id)
            AppResult.Success(
                EmployeeAnalytics(
                    employeeId = dto.employeeId,
                    employeeName = dto.employeeName,
                    role = dto.role,
                    forecastDaysToComplete = dto.forecastDaysToComplete,
                    sprintDaysLeft = dto.sprintDaysLeft,
                    delayDays = dto.delayDays,
                    aiInsight = dto.aiInsight,
                    tasks = dto.tasks.map { task ->
                        EmployeeTask(
                            id = task.id,
                            title = task.title,
                            status = TaskStatus.fromString(task.status),
                        )
                    },
                )
            )
        } catch (e: HttpException) {
            when (e.code()) {
                401 -> AppResult.Failure(AppError.Auth)
                else -> AppResult.Failure(AppError.Unknown(e.message()))
            }
        } catch (e: Exception) {
            AppResult.Failure(AppError.Network)
        }
    }
}
