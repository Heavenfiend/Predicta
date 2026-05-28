package com.predicta.app.data.remote

import com.predicta.app.data.remote.dto.CreateTaskRequest
import com.predicta.app.data.remote.dto.CreateTaskResponse
import com.predicta.app.data.remote.dto.EmployeeAnalyticsResponse
import com.predicta.app.data.remote.dto.EmployeeDetailResponse
import com.predicta.app.data.remote.dto.HealthResponse
import com.predicta.app.data.remote.dto.LoginRequest
import com.predicta.app.data.remote.dto.LoginResponse
import com.predicta.app.data.remote.dto.MeResponse
import com.predicta.app.data.remote.dto.ProjectStatusResponse
import com.predicta.app.data.remote.dto.ReassignTaskRequest
import com.predicta.app.data.remote.dto.ReassignTaskResponse
import com.predicta.app.data.remote.dto.RegisterRequest
import com.predicta.app.data.remote.dto.RegisterResponse
import com.predicta.app.data.remote.dto.TeamInsightsResponse
import com.predicta.app.data.remote.dto.TeamMemberResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Central Retrofit API interface for the Predicta backend.
 * Authorization header is injected automatically by [AuthInterceptor]
 * for all api endpoints except register and login.
 */
interface PredictaApi {

    // ── Health ───────────────────────────────────────────────────────────────

    @GET("health")
    suspend fun health(): HealthResponse

    // ── Auth ─────────────────────────────────────────────────────────────────

    @POST("api/auth/register")
    suspend fun register(@Body body: RegisterRequest): RegisterResponse

    @POST("api/auth/login")
    suspend fun login(@Body body: LoginRequest): LoginResponse

    @GET("api/auth/me")
    suspend fun getMe(): MeResponse

    // ── Project ──────────────────────────────────────────────────────────────

    @GET("api/project/status")
    suspend fun getProjectStatus(): ProjectStatusResponse

    // ── Team ─────────────────────────────────────────────────────────────────

    @GET("api/team/velocity")
    suspend fun getTeamVelocity(): List<TeamMemberResponse>

    @GET("api/team/insights")
    suspend fun getTeamInsights(): TeamInsightsResponse

    // ── Employee ─────────────────────────────────────────────────────────────

    @GET("api/employee/{id}")
    suspend fun getEmployee(@Path("id") id: String): EmployeeDetailResponse

    @GET("api/employee/{id}/analytics")
    suspend fun getEmployeeAnalytics(@Path("id") id: String): EmployeeAnalyticsResponse

    // ── Tasks ────────────────────────────────────────────────────────────────

    @POST("api/tasks/create")
    suspend fun createTask(@Body body: CreateTaskRequest): CreateTaskResponse

    @POST("api/tasks/reassign")
    suspend fun reassignTask(@Body body: ReassignTaskRequest): ReassignTaskResponse
}
