package com.predicta.app.feature_employees.presentation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.predicta.app.feature_employees.domain.model.Health
import com.predicta.app.feature_employees.domain.model.TeamMember
import com.predicta.app.ui.components.AnimatedNumberText
import com.predicta.app.ui.modifier.liquidGlass
import com.predicta.app.ui.modifier.pressScale
import com.predicta.app.ui.theme.PredictaShapes
import com.predicta.app.ui.theme.PrimaryBlue
import com.predicta.app.ui.theme.SemanticCritical
import com.predicta.app.ui.theme.SemanticSuccess
import com.predicta.app.ui.theme.SemanticWarning
import org.koin.androidx.compose.koinViewModel

@Composable
fun TeamVelocityScreen(
    onNavigateToEmployeeCard: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EmployeeViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { action ->
            when (action) {
                is EmployeeEffect.GoToEmployeeCard -> {
                    onNavigateToEmployeeCard(action.employeeId)
                }
            }
        }
    }

    TeamVelocityContent(
        state = state,
        onEvent = viewModel::onEvent,
        modifier = modifier,
    )
}

@Composable
private fun TeamVelocityContent(
    state: EmployeeState,
    onEvent: (EmployeeEvent) -> Unit,
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
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Text(
                text = "Анализ темпа работы",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "Бэкенд-команда",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp),
            )
        }

        // ── Team members ────────────────────────────────────────────────
        items(state.teamMembers) { member ->
            VelocityCard(
                name = member.name,
                role = member.role,
                done = member.doneCount,
                total = member.totalCount,
                health = member.health,
                onClick = { onEvent(EmployeeEvent.SelectEmployee(member.id)) },
            )
        }

        // ── Summary card ────────────────────────────────────────────────
        item {
            SummaryCard(teamMembers = state.teamMembers)
        }

        // ── Team Insight Card ───────────────────────────────────────────
        if (state.teamInsight.isNotBlank()) {
            item {
                Text(
                    text = "ИИ-Инсайт команды",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            item {
                TeamInsightCard(insight = state.teamInsight)
            }
        }

        item { Spacer(modifier = Modifier.height(8.dp)) }
    }
}

@Composable
private fun VelocityCard(
    name: String,
    role: String,
    done: Int,
    total: Int,
    health: Health,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val progress = if (total > 0) done.toFloat() / total else 0f
    
    val barColor = when (health) {
        Health.GOOD -> SemanticSuccess
        Health.NORMAL -> PrimaryBlue
        Health.BAD -> SemanticCritical
    }
    
    val healthText = when (health) {
        Health.GOOD -> "Темп: Отличный"
        Health.NORMAL -> "Темп: В норме"
        Health.BAD -> "Темп: Риск отставания"
    }

    val isActive = health == Health.BAD
    val interactionSource = remember { MutableInteractionSource() }

    // Animate progress
    var targetProgress by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(progress) { targetProgress = progress }
    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 900),
        label = "velocity_$name",
    )

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
                isActive = isActive,
            )
            .pressScale(interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Avatar placeholder
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(barColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null,
                        tint = barColor,
                        modifier = Modifier.size(24.dp),
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = role,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                // Progress numbers
                AnimatedNumberText(
                    value = done,
                    suffix = " / $total",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                    color = barColor,
                )

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = "Подробнее",
                    tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    modifier = Modifier.size(24.dp),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Horizontal velocity bar
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp)),
                color = barColor,
                trackColor = barColor.copy(alpha = 0.12f),
                strokeCap = StrokeCap.Round,
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Label under the bar
            Text(
                text = healthText,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = barColor,
            )
        }
    }
}

@Composable
private fun SummaryCard(
    teamMembers: List<TeamMember>,
    modifier: Modifier = Modifier,
) {
    val totalTotal = teamMembers.sumOf { it.totalCount }
    val totalDone = teamMembers.sumOf { it.doneCount }
    val totalRemaining = totalTotal - totalDone

    Card(
        shape = PredictaShapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
            .fillMaxWidth()
            .liquidGlass(
                shape = PredictaShapes.medium,
                blurRadius = 0.dp,
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
        ) {
            Text(
                text = "Общая статистика команды",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
            )

            Spacer(modifier = Modifier.height(12.dp))

            SummaryRow(
                label = "Всего задач в спринте",
                value = "$totalTotal",
            )
            SummaryRow(
                label = "Выполнено",
                value = "$totalDone",
            )
            SummaryRow(
                label = "Осталось",
                value = "$totalRemaining",
            )
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
        )
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
