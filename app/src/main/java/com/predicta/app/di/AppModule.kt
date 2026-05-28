package com.predicta.app.di

import com.predicta.app.core.network.NetworkMonitor
import com.predicta.app.feature_auth.data.repository.AuthRepositoryImpl
import com.predicta.app.feature_auth.data.session.UserSessionManager
import com.predicta.app.feature_auth.domain.repository.AuthRepository
import com.predicta.app.feature_auth.presentation.AuthViewModel
import com.predicta.app.feature_dashboard.data.repository.DashboardRepositoryImpl
import com.predicta.app.feature_dashboard.domain.repository.DashboardRepository
import com.predicta.app.feature_dashboard.presentation.DashboardViewModel
import com.predicta.app.feature_employees.data.repository.EmployeeRepositoryImpl
import com.predicta.app.feature_employees.domain.repository.EmployeeRepository
import com.predicta.app.feature_employees.presentation.EmployeeCardViewModel
import com.predicta.app.feature_employees.presentation.EmployeeViewModel
import com.predicta.app.feature_settings.data.repository.AppSettingsRepository
import com.predicta.app.feature_settings.presentation.SettingsViewModel
import com.predicta.app.feature_tasks.domain.usecase.ReassignTaskUseCase
import com.predicta.app.feature_tasks.presentation.TaskReassignmentViewModel
import com.predicta.app.feature_tasks.presentation.TaskViewModel
import com.predicta.app.ui.AppViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * Top-level application module for Koin dependency injection.
 * Registers app runtime components, feature repositories, viewmodels, and usecases.
 */
val appModule = module {

    // ── App Runtime ─────────────────────────────────────────────────────
    single { NetworkMonitor(androidContext()) }
    single { UserSessionManager(androidContext()) }
    viewModel { AppViewModel(networkMonitor = get(), sessionManager = get()) }

    // ── Auth Feature ────────────────────────────────────────────────────
    single<AuthRepository> { 
        AuthRepositoryImpl(
            api = get(),
            json = get(),
            sessionManager = get()
        ) 
    }
    viewModel { 
        AuthViewModel(
            repository = get(),
            sessionManager = get()
        ) 
    }

    // ── App Settings ────────────────────────────────────────────────────
    single { AppSettingsRepository(androidContext()) }

    // ── feature_dashboard ───────────────────────────────────────────────
    single<DashboardRepository> { DashboardRepositoryImpl(api = get()) }
    viewModel { DashboardViewModel(repository = get()) }

    // ── feature_employees (Team Velocity + Employee Card) ───────────────
    single<EmployeeRepository> { EmployeeRepositoryImpl(api = get()) }
    viewModel { EmployeeViewModel(employeeRepository = get(), dashboardRepository = get()) }
    viewModel { EmployeeCardViewModel(savedStateHandle = get(), employeeRepository = get()) }

    // ── feature_tasks ───────────────────────────────────────────────────
    factory { ReassignTaskUseCase(api = get()) }
    viewModel { TaskViewModel(employeeRepository = get(), api = get()) }
    viewModel { 
        TaskReassignmentViewModel(
            savedStateHandle = get(),
            employeeRepository = get(),
            reassignTaskUseCase = get()
        ) 
    }

    // ── feature_settings ────────────────────────────────────────────────
    viewModel { SettingsViewModel(settingsRepository = get(), sessionManager = get()) }
}
