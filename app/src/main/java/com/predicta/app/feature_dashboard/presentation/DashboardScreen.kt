package com.predicta.app.feature_dashboard.presentation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.predicta.app.ui.components.AnimatedNumberText
import com.predicta.app.ui.modifier.liquidGlass
import com.predicta.app.ui.theme.BackgroundCritical
import com.predicta.app.ui.theme.BackgroundSuccess
import com.predicta.app.ui.theme.PredictaShapes
import com.predicta.app.ui.theme.PrimaryBlue
import com.predicta.app.ui.theme.SemanticCritical
import com.predicta.app.ui.theme.SemanticSuccess
import com.predicta.app.ui.theme.SemanticWarning
import org.koin.androidx.compose.koinViewModel

@Composable
fun DashboardScreen(
    onNavigateToTeamVelocity: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { action ->
            when (action) {
                DashboardEffect.GoToTeamVelocity -> onNavigateToTeamVelocity()
            }
        }
    }

    DashboardContent(
        state = state,
        modifier = modifier,
    )
}

@Composable
private fun DashboardContent(
    state: DashboardState,
    modifier: Modifier = Modifier,
) {
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

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        // ── Section: Sprint Status Widget ────────────────────────────────
        item {
            SprintStatusCard(
                sprintName = state.sprintName,
                isAtRisk = state.isAtRisk,
                delayDays = state.delayDays,
                trackName = state.trackName,
                completionPct = state.completionPct,
                daysRemaining = state.daysRemaining,
            )
        }

        // ── Section: AI Analysis & Recommendations ──────────────────────
        if (state.aiAdvice.isNotBlank() || state.riskMessage.isNotBlank()) {
            item {
                Text(
                    text = "ИИ-Аналитика спринта",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            item {
                AiRecommendationCard(
                    riskMessage = state.riskMessage,
                    aiAdvice = state.aiAdvice,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // ── Section: Team Insight ───────────────────────────────────────
        if (state.teamInsight.isNotBlank()) {
            item {
                Text(
                    text = "Состояние команды",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            item {
                TeamInsightCard(
                    insight = state.teamInsight,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Bottom spacer for comfortable scrolling above the nav bar
        item { Spacer(modifier = Modifier.height(8.dp)) }
    }
}

@Composable
private fun SprintStatusCard(
    sprintName: String,
    isAtRisk: Boolean,
    delayDays: Int,
    trackName: String,
    completionPct: Double,
    daysRemaining: Int,
    modifier: Modifier = Modifier,
) {
    val statusColor = if (isAtRisk) SemanticCritical else SemanticSuccess
    val textColor = MaterialTheme.colorScheme.primary
    val subTextColor = MaterialTheme.colorScheme.onSurfaceVariant

    // Animated completion progress
    var targetProgress by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(completionPct) { targetProgress = (completionPct / 100.0).toFloat() }
    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 1000),
        label = "sprint_progress",
    )

    Card(
        shape = PredictaShapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
            .fillMaxWidth()
            .liquidGlass(
                shape = PredictaShapes.medium,
                blurRadius = 0.dp,
                liquidIntensity = 0.9f,
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = sprintName,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (isAtRisk) {
                    "Риск срыва дедлайна $trackName на $delayDays дн."
                } else {
                    "Спринт идет по графику"
                },
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = if (isAtRisk) statusColor else textColor,
            )

            Spacer(modifier = Modifier.height(20.dp))

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AnimatedNumberText(
                        value = (animatedProgress * 100).toInt(),
                        prefix = "Выполнено: ",
                        suffix = "%",
                        style = MaterialTheme.typography.labelMedium,
                        color = subTextColor,
                    )
                    Text(
                        text = "Осталось дней: $daysRemaining",
                        style = MaterialTheme.typography.labelMedium,
                        color = subTextColor,
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = statusColor,
                    trackColor = statusColor.copy(alpha = 0.15f),
                    strokeCap = StrokeCap.Round,
                )
            }
        }
    }
}

@Composable
private fun AiRecommendationCard(
    riskMessage: String,
    aiAdvice: String,
    modifier: Modifier = Modifier,
) {
    Card(
        shape = PredictaShapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier.liquidGlass(
            shape = PredictaShapes.medium,
            blurRadius = 0.dp,
            liquidIntensity = 0.9f,
        ),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            if (riskMessage.isNotBlank()) {
                Text(
                    text = "Анализ рисков:",
                    style = MaterialTheme.typography.titleSmall,
                    color = SemanticCritical,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = riskMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            if (aiAdvice.isNotBlank()) {
                Text(
                    text = "ИИ-Рекомендация:",
                    style = MaterialTheme.typography.titleSmall,
                    color = PrimaryBlue,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = aiAdvice,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@Composable
private fun TeamInsightCard(
    insight: String,
    modifier: Modifier = Modifier,
) {
    Card(
        shape = PredictaShapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier.liquidGlass(
            shape = PredictaShapes.medium,
            blurRadius = 0.dp,
            liquidIntensity = 0.9f,
        ),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Состояние команды:",
                style = MaterialTheme.typography.titleSmall,
                color = SemanticWarning,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = insight,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}
