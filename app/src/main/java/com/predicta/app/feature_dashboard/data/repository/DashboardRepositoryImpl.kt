package com.predicta.app.feature_dashboard.data.repository

import com.predicta.app.core.error.AppError
import com.predicta.app.core.error.AppResult
import com.predicta.app.data.remote.PredictaApi
import com.predicta.app.feature_dashboard.domain.model.ProjectStatus
import com.predicta.app.feature_dashboard.domain.repository.DashboardRepository
import retrofit2.HttpException

class DashboardRepositoryImpl(
    private val api: PredictaApi,
) : DashboardRepository {

    override suspend fun getProjectStatus(): AppResult<ProjectStatus> {
        return try {
            val response = api.getProjectStatus()
            AppResult.Success(
                ProjectStatus(
                    sprintName = response.sprintName,
                    completionPct = response.completionPct,
                    delayDays = response.delayDays,
                    isAtRisk = response.isAtRisk,
                    riskMessage = response.riskMessage,
                    aiAdvice = response.aiAdvice,
                    trackName = response.trackName,
                    daysRemaining = response.daysRemaining,
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

    override suspend fun getTeamInsights(): AppResult<String> {
        return try {
            val response = api.getTeamInsights()
            AppResult.Success(response.aiInsight)
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
