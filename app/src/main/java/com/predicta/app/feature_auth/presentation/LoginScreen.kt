package com.predicta.app.feature_auth.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.predicta.app.R
import com.predicta.app.ui.modifier.liquidGlass
import com.predicta.app.ui.modifier.pressScale
import com.predicta.app.ui.theme.PredictaShapes
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(
    onNavigateToDashboard: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var passwordVisible by remember { mutableStateOf(false) }
    val loginInteraction = remember { MutableInteractionSource() }
    val demoInteraction = remember { MutableInteractionSource() }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                AuthEffect.Authenticated -> {
                    viewModel.onEvent(AuthEvent.ResetSuccessState)
                    onNavigateToDashboard()
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(id = R.drawable.starting_screen),
            contentDescription = null,
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer {
                    alpha = 0.16f
                },
            contentScale = ContentScale.Crop,
        )

        LoginFormCard(
            state = state,
            passwordVisible = passwordVisible,
            onPasswordVisibilityChanged = { passwordVisible = it },
            onNavigateToRegister = onNavigateToRegister,
            onNavigateToForgotPassword = onNavigateToForgotPassword,
            onEvent = viewModel::onEvent,
            loginInteraction = loginInteraction,
            demoInteraction = demoInteraction,
            modifier = Modifier.padding(24.dp)
        )
    }
}

@Composable
private fun LoginFormCard(
    state: AuthState,
    passwordVisible: Boolean,
    onPasswordVisibilityChanged: (Boolean) -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onEvent: (AuthEvent) -> Unit,
    loginInteraction: MutableInteractionSource,
    demoInteraction: MutableInteractionSource,
    modifier: Modifier = Modifier,
) {
    Card(
        shape = PredictaShapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
        ),
        modifier = modifier
            .fillMaxWidth()
            .liquidGlass(
                shape = PredictaShapes.large,
                blurRadius = 0.dp,
                isActive = true,
            ),
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Predicta Logo",
                modifier = Modifier.size(104.dp),
                contentScale = ContentScale.Fit,
            )

            Spacer(modifier = Modifier.height(26.dp))

            Text(
                text = "Добро пожаловать в Predicta",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "ИИ-ассистент СДМ",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(28.dp))

            OutlinedTextField(
                value = state.email,
                onValueChange = { onEvent(AuthEvent.EmailChanged(it)) },
                label = { Text("Почта") },
                modifier = Modifier.fillMaxWidth(),
                shape = PredictaShapes.medium,
                singleLine = true,
                isError = state.emailError != null,
                supportingText = state.emailError?.let { { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = state.password,
                onValueChange = { onEvent(AuthEvent.PasswordChanged(it)) },
                label = { Text("Пароль") },
                modifier = Modifier.fillMaxWidth(),
                shape = PredictaShapes.medium,
                singleLine = true,
                visualTransformation = if (passwordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                trailingIcon = {
                    IconButton(onClick = { onPasswordVisibilityChanged(!passwordVisible) }) {
                        Icon(
                            imageVector = if (passwordVisible) {
                                Icons.Outlined.VisibilityOff
                            } else {
                                Icons.Outlined.Visibility
                            },
                            contentDescription = null,
                        )
                    }
                },
                isError = state.passwordError != null,
                supportingText = state.passwordError?.let { { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            )

            if (state.globalError != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = state.globalError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            Spacer(modifier = Modifier.height(26.dp))

            Button(
                onClick = { onEvent(AuthEvent.LoginSubmit) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .pressScale(loginInteraction),
                shape = PredictaShapes.medium,
                enabled = !state.isLoading,
                interactionSource = loginInteraction,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = "Войти",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = {
                    onEvent(AuthEvent.LoginDemoSubmit)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .pressScale(demoInteraction),
                shape = PredictaShapes.medium,
                interactionSource = demoInteraction,
                enabled = !state.isLoading,
            ) {
                Text(
                    text = "Войти в демо",
                    fontWeight = FontWeight.SemiBold,
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier
                    .clickable(onClick = onNavigateToRegister)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Нет аккаунта? ",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Normal,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = "Зарегистрироваться",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

private const val LOGIN_INTRO_DURATION_MS = 1_850L
