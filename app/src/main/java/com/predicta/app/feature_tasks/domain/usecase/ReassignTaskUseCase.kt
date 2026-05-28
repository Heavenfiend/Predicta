package com.predicta.app.feature_tasks.domain.usecase

import com.predicta.app.core.error.AppError
import com.predicta.app.core.error.AppResult
import com.predicta.app.data.remote.PredictaApi
import com.predicta.app.data.remote.dto.ReassignTaskRequest
import com.predicta.app.data.remote.dto.ReassignTaskResponse
import retrofit2.HttpException

class ReassignTaskUseCase(
    private val api: PredictaApi,
) {
    suspend operator fun invoke(taskId: String, newExecutorId: String): AppResult<ReassignTaskResponse> {
        return try {
            val response = api.reassignTask(
                ReassignTaskRequest(taskId = taskId, newExecutorId = newExecutorId)
            )
            AppResult.Success(response)
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
