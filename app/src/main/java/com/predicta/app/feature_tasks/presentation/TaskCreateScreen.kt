package com.predicta.app.feature_tasks.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.text.style.TextAlign
import com.predicta.app.feature_employees.domain.model.Health
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.predicta.app.ui.modifier.liquidGlass
import com.predicta.app.ui.theme.PredictaShapes
import com.predicta.app.ui.theme.PrimaryBlue
import com.predicta.app.ui.theme.SemanticCritical
import com.predicta.app.ui.theme.SemanticSuccess
import com.predicta.app.ui.theme.SemanticWarning
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskCreateScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TaskViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            viewModel.onEvent(TaskEvent.ResetSuccessState)
            onNavigateBack()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (state.isLoading && state.employees.isEmpty()) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 3.dp,
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.Center),
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
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
                            text = "Новая задача",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }

                // Title Input
                item {
                    OutlinedTextField(
                        value = state.taskTitle,
                        onValueChange = { viewModel.onEvent(TaskEvent.UpdateTitle(it)) },
                        label = { Text("Название задачи") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = PredictaShapes.medium,
                        singleLine = true,
                    )
                }

                // Description Input
                item {
                    OutlinedTextField(
                        value = state.taskDescription,
                        onValueChange = { viewModel.onEvent(TaskEvent.UpdateDescription(it)) },
                        label = { Text("Описание (необязательно)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        shape = PredictaShapes.medium,
                    )
                }

                // Assignee Selection Dropdown
                item {
                    Column {
                        Text(
                            text = "Исполнитель",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(PredictaShapes.medium)
                                .border(1.dp, MaterialTheme.colorScheme.outline, PredictaShapes.medium)
                                .clickable { viewModel.onEvent(TaskEvent.ToggleDropdown) }
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = state.selectedEmployee?.name ?: "Выберите сотрудника",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (state.selectedEmployee != null) {
                                        MaterialTheme.colorScheme.onSurface
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            
                            DropdownMenu(
                                expanded = state.isDropdownExpanded,
                                onDismissRequest = { viewModel.onEvent(TaskEvent.DismissDropdown) },
                                modifier = Modifier.fillMaxWidth(0.85f)
                            ) {
                                state.employees.forEach { employee ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(employee.name)
                                                Text(
                                                    text = "${employee.doneCount}/${employee.totalCount}",
                                                    color = when (employee.health) {
                                                        Health.GOOD -> SemanticSuccess
                                                        Health.NORMAL -> PrimaryBlue
                                                        Health.BAD -> SemanticCritical
                                                    },
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        },
                                        onClick = { viewModel.onEvent(TaskEvent.SelectEmployee(employee)) }
                                    )
                                }
                            }
                        }
                    }
                }

                // AI Insight overload alert
                if (state.aiInsight != null) {
                    item {
                        Card(
                            shape = PredictaShapes.medium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .liquidGlass(
                                    shape = PredictaShapes.medium,
                                    blurRadius = 0.dp,
                                    isActive = true,
                                ),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.AutoAwesome,
                                        contentDescription = null,
                                        tint = SemanticWarning,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Text(
                                        text = "Предупреждение Predicta AI",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = SemanticWarning
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(10.dp))
                                
                                Text(
                                    text = state.aiInsight ?: "",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                // Suggested actions
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    if (state.suggestedEmployee != null) {
                                        Button(
                                            onClick = { viewModel.onEvent(TaskEvent.SelectSuggestedEmployee) },
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = PredictaShapes.medium,
                                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.Person,
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "Переназначить на ${state.suggestedEmployee!!.name}",
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                    
                                    OutlinedButton(
                                        onClick = { viewModel.onEvent(TaskEvent.ForceSubmitTask) },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = PredictaShapes.medium,
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = SemanticCritical)
                                    ) {
                                        Text(
                                            text = "Все равно назначить",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Error Message
                if (state.error != null) {
                    item {
                        Text(
                            text = state.error ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Action Button (standard submit)
                if (state.aiInsight == null) {
                    item {
                        Button(
                            onClick = { viewModel.onEvent(TaskEvent.SubmitTask) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = PredictaShapes.medium,
                            enabled = !state.isLoading,
                        ) {
                            if (state.isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Text(
                                    text = "Назначить задачу",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
