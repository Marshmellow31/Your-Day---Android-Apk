package com.yourday.app.ui.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yourday.app.ui.theme.Background
import com.yourday.app.ui.theme.Primary
import com.yourday.app.ui.theme.Secondary
import com.yourday.app.ui.viewmodel.AuthState
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    authState: AuthState,
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToOnboarding: () -> Unit
) {
    val alpha = remember { Animatable(0f) }
    val scale = remember { Animatable(0.8f) }
    var readyToNavigate by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        alpha.animateTo(1f, animationSpec = tween(800))
        scale.animateTo(1f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        delay(800)
        readyToNavigate = true
    }

    LaunchedEffect(authState, readyToNavigate) {
        if (!readyToNavigate) return@LaunchedEffect
        if (authState is AuthState.Loading) return@LaunchedEffect
        delay(200)
        when (authState) {
            is AuthState.LoggedIn -> onNavigateToHome()
            else -> onNavigateToLogin()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        contentAlignment = Alignment.Center
    ) {
        // Background glow
        Box(
            modifier = Modifier
                .size(300.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(Primary.copy(alpha = 0.15f), Background)
                    )
                )
                .alpha(alpha.value)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .alpha(alpha.value)
                .scale(scale.value)
        ) {
            Text(
                text = "Your Day",
                style = MaterialTheme.typography.displaySmall.copy(
                    brush = Brush.linearGradient(listOf(Primary, Secondary)),
                    fontWeight = FontWeight.Bold
                ),
                fontSize = 42.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Study smarter. Stay focused.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
