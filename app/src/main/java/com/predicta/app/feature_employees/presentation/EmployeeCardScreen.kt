package com.predicta.app.feature_employees.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.predicta.app.feature_employees.domain.model.EmployeeTask
import com.predicta.app.feature_employees.domain.model.Health
import com.predicta.app.feature_employees.domain.model.TaskStatus
import com.predicta.app.ui.components.AnimatedNumberText
import com.predicta.app.ui.modifier.liquidGlass
import com.predicta.app.ui.modifier.pressScale
import com.predicta.app.ui.theme.PredictaShapes
import com.predicta.app.ui.theme.PrimaryBlue
import com.predicta.app.ui.theme.SemanticCritical
import com.predicta.app.ui.theme.SemanticSuccess
import com.predicta.app.ui.theme.SemanticWarning
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun EmployeeCardScreen(
    employeeId: String,
    onNavigateBack: () -> Unit,
    onNavigateToReassign: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EmployeeCardViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    if (state.isLoading) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 3.dp,
                modifier = Modifier.size(40.dp),
            )
        }
        return
    }

    EmployeeCardContent(
        state = state,
        onBack = onNavigateBack,
        onReassign = onNavigateToReassign,
        modifier = modifier,
    )
}

@Composable
private fun EmployeeCardContent(
    state: EmployeeCardState,
    onBack: () -> Unit,
    onReassign: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showAiInsight by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(300)
        showAiInsight = true
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // ── Back button + header ────────────────────────────────────────
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Назад",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Карточка сотрудника",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }

        // ── Employee header card ────────────────────────────────────────
        item {
            EmployeeHeaderCard(
                name = state.name,
                role = state.role,
                telegramNick = state.telegramNick,
                done = state.doneCount,
                total = state.totalCount,
                health = state.health,
            )
        }

        // ── Forecast / Deadline analytics block ────────────────────────
        item {
            ForecastCard(
                forecastDays = state.forecastDaysToComplete,
                sprintDaysLeft = state.sprintDaysLeft,
                delayDays = state.delayDays,
            )
        }

        // ── AI Insight card ─────────────────────────────────────────────
        if (state.aiInsight.isNotBlank() || state.analyticsAiInsight.isNotBlank()) {
            item {
                AnimatedVisibility(
                    visible = showAiInsight,
                    enter = fadeIn(tween(600)) + slideInVertically(tween(600)),
                ) {
                    AiInsightCard(
                        insight = state.aiInsight.ifBlank { state.analyticsAiInsight }
                    )
                }
            }
        }

        // ── Task list ───────────────────────────────────────────────────
        if (state.tasks.isNotEmpty()) {
            item {
                Text(
                    text = "Текущие задачи",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }

            items(
                items = state.tasks,
                key = { it.id },
            ) { task ->
                TaskCard(
                    task = task,
                    onReassign = { onReassign(task.id) },
                )
            }
        }

        item { Spacer(modifier = Modifier.height(8.dp)) }
    }
}

@Composable
private fun EmployeeHeaderCard(
    name: String,
    role: String,
    telegramNick: String,
    done: Int,
    total: Int,
    health: Health,
    modifier: Modifier = Modifier,
) {
    val barColor = when (health) {
        Health.GOOD -> SemanticSuccess
        Health.NORMAL -> PrimaryBlue
        Health.BAD -> SemanticCritical
    }

    Card(
        shape = PredictaShapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
            .fillMaxWidth()
            .liquidGlass(
                shape = PredictaShapes.medium,
                blurRadius = 0.dp,
                tintColor = barColor,
                tintAlpha = 0.08f,
            ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(barColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = null,
                    tint = barColor,
                    modifier = Modifier.size(32.dp),
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = role,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (telegramNick.isNotBlank()) {
                    Text(
                        text = "Telegram: @${telegramNick.removePrefix("@")}",
                        style = MaterialTheme.typography.bodySmall,
                        color = PrimaryBlue,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                AnimatedNumberText(
                    value = done,
                    suffix = " / $total",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                    color = barColor,
                )
                Text(
                    text = "задач",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun ForecastCard(
    forecastDays: Int,
    sprintDaysLeft: Int,
    delayDays: Int,
    modifier: Modifier = Modifier,
) {
    val isDelayed = delayDays > 0
    val color = if (isDelayed) SemanticCritical else SemanticSuccess

    Card(
        shape = PredictaShapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
            .fillMaxWidth()
            .liquidGlass(
                shape = PredictaShapes.medium,
                blurRadius = 0.dp,
                tintColor = color,
                tintAlpha = 0.05f
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Schedule,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp),
                )
                Text(
                    text = "Прогноз дедлайна",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Прогноз завершения",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$forecastDays дн.",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Column {
                    Text(
                        text = "Дней до конца спринта",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$sprintDaysLeft дн.",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Отставание",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (isDelayed) "+$delayDays дн." else "0 дн.",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = if (isDelayed) {
                    "Внимание: Прогноз завершения задач превышает лимит спринта на $delayDays дн."
                } else {
                    "Сотрудник завершит все назначенные задачи до конца спринта."
                },
                style = MaterialTheme.typography.bodySmall,
                color = color,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun AiInsightCard(
    insight: String,
    modifier: Modifier = Modifier,
) {
    Card(
        shape = PredictaShapes.medium,
        modifier = modifier
            .fillMaxWidth()
            .liquidGlass(
                shape = PredictaShapes.medium,
                blurRadius = 0.dp,
                isActive = true,
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f),
                        ),
                    ),
                )
                .padding(20.dp),
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AutoAwesome,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp),
                    )
                    Text(
                        text = "Аналитика Predicta AI",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = insight,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight,
                )
            }
        }
    }
}

@Composable
private fun TaskCard(
    task: EmployeeTask,
    onReassign: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val statusColor = when (task.status) {
        TaskStatus.DONE -> SemanticSuccess
        TaskStatus.IN_PROGRESS -> SemanticWarning
        TaskStatus.TODO -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val statusLabel = when (task.status) {
        TaskStatus.DONE -> "Выполнено"
        TaskStatus.IN_PROGRESS -> "В работе"
        TaskStatus.TODO -> "Ожидает"
    }

    val canReassign = task.status == TaskStatus.IN_PROGRESS || task.status == TaskStatus.TODO

    Card(
        shape = PredictaShapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
            .liquidGlass(
                shape = PredictaShapes.medium,
                blurRadius = 0.dp,
                isActive = canReassign,
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Status dot
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(statusColor),
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = statusLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
            }

            if (canReassign) {
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onReassign,
                    modifier = Modifier.fillMaxWidth(),
                    shape = PredictaShapes.medium,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary,
                    ),
                ) {
                    Icon(
                        imageVector = Icons.Filled.SwapHoriz,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Перераспределить",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}
