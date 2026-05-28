package com.predicta.app.feature_tasks.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.predicta.app.feature_employees.domain.model.TeamMember
import com.predicta.app.ui.modifier.liquidGlass
import com.predicta.app.ui.theme.BackgroundCritical
import com.predicta.app.ui.theme.BackgroundSuccess
import com.predicta.app.ui.theme.PredictaShapes
import com.predicta.app.ui.theme.PrimaryBlue
import com.predicta.app.ui.theme.SemanticCritical
import com.predicta.app.ui.theme.SemanticSuccess
import com.predicta.app.ui.theme.SurfaceWhite
import org.koin.androidx.compose.koinViewModel

@Composable
fun TaskReassignmentScreen(
    taskId: String,
    onNavigateBack: () -> Unit,
    onReassignmentComplete: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TaskReassignmentViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.effects.collect { action ->
            when (action) {
                TaskReassignmentEffect.GoToDashboard -> onReassignmentComplete()
            }
        }
    }

    if (state.isLoading && state.taskId.isEmpty()) {
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

    if (state.error != null && state.taskId.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = state.error ?: "Задача не найдена",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(24.dp)
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
        // ── Back button + header ────────────────────────────────────────
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Назад",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Перераспределение задачи",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }

        // ── Task info card ──────────────────────────────────────────────
        item {
            Card(
                shape = PredictaShapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
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
                        text = "Задача",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = state.taskTitle,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }
        }

        // ── Transfer visualization ──────────────────────────────────────
        item {
            val selectedExecutor = state.toExecutors.find { it.id == state.selectedExecutorId }
            TransferVisualization(
                fromName = state.fromName,
                toName = selectedExecutor?.name ?: "Не выбран",
            )
        }

        // ── Selection list ──────────────────────────────────────────────
        if (state.toExecutors.isNotEmpty() && !state.isReassigned) {
            item {
                Text(
                    text = "Выберите нового исполнителя",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            
            items(state.toExecutors) { member ->
                val isSelected = state.selectedExecutorId == member.id
                val borderModifier = if (isSelected) {
                    Modifier.border(2.dp, PrimaryBlue, PredictaShapes.medium)
                } else Modifier
                
                Card(
                    shape = PredictaShapes.medium,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(borderModifier)
                        .clickable { viewModel.onEvent(TaskReassignmentEvent.SelectExecutor(member.id)) }
                        .liquidGlass(
                            shape = PredictaShapes.medium,
                            blurRadius = 0.dp,
                            tintColor = if (isSelected) PrimaryBlue else Color.Gray,
                            tintAlpha = if (isSelected) 0.08f else 0.02f,
                            isActive = isSelected
                        )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(PrimaryBlue.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Person,
                                contentDescription = null,
                                tint = PrimaryBlue,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = member.name,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = member.role,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        Text(
                            text = "${member.doneCount} / ${member.totalCount}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // ── Confirm button or Success state ─────────────────────────────
        item {
            if (!state.canReassign && !state.isReassigned) {
                Text(
                    text = "Эту задачу нельзя перераспределить",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
            } else if (!state.isReassigned) {
                Button(
                    onClick = {
                        viewModel.onEvent(TaskReassignmentEvent.ConfirmReassignment)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = PredictaShapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                    ),
                    enabled = state.selectedExecutorId != null && !state.isLoading,
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp,
                    ),
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Icon(
                            imageVector = Icons.Filled.SwapHoriz,
                            contentDescription = null,
                            modifier = Modifier.size(22.dp),
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Подтвердить перераспределение",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            } else {
                SuccessCard(onGoToDashboard = { viewModel.onEvent(TaskReassignmentEvent.CompleteReassignment) })
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
private fun TransferVisualization(
    fromName: String,
    toName: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // From
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(BackgroundCritical),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = null,
                    tint = SemanticCritical,
                    modifier = Modifier.size(32.dp),
                )
            }
            Text(
                text = fromName,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = SemanticCritical,
                modifier = Modifier.padding(top = 8.dp),
            )
            Text(
                text = "Перегружен",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        // Arrow
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(32.dp),
        )

        // To
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(BackgroundSuccess),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = null,
                    tint = SemanticSuccess,
                    modifier = Modifier.size(32.dp),
                )
            }
            Text(
                text = toName,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = SemanticSuccess,
                modifier = Modifier.padding(top = 8.dp),
            )
            Text(
                text = "Свободен",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun SuccessCard(
    onGoToDashboard: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn(tween(500)) + scaleIn(tween(500)),
    ) {
        Card(
            shape = PredictaShapes.large,
            modifier = modifier
                .fillMaxWidth()
                .liquidGlass(
                    shape = PredictaShapes.large,
                    blurRadius = 0.dp,
                    tintColor = SemanticSuccess,
                    tintAlpha = 0.08f,
                    isActive = true,
                ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(
                            listOf(SemanticSuccess, SemanticSuccess.copy(alpha = 0.85f)),
                        ),
                    )
                    .padding(28.dp),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(48.dp),
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Задача перераспределена!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Сроки в Jira обновлены.\nПроект выровнен.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center,
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = onGoToDashboard,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = SemanticSuccess,
                        ),
                        shape = PredictaShapes.medium,
                    ) {
                        Text(
                            text = "Вернуться на дашборд",
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }
    }
}
