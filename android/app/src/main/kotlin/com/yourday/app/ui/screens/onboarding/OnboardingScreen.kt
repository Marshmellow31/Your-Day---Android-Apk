package com.yourday.app.ui.screens.onboarding

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yourday.app.ui.components.AppButton
import com.yourday.app.ui.theme.*
import kotlinx.coroutines.launch

private data class OnboardingPage(val emoji: String, val title: String, val subtitle: String, val accentColor: androidx.compose.ui.graphics.Color)

private val pages = listOf(
    OnboardingPage("📚", "Study Smarter", "Organize subjects, topics, and tasks in one beautiful place.", Primary),
    OnboardingPage("📊", "Track Progress", "See your weekly streaks and completion rates at a glance.", Secondary),
    OnboardingPage("🎯", "Hit Your Goals", "Set personal goals and let the smart scheduler do the rest.", Tertiary)
)

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    val pagerState = rememberPagerState { pages.size }
    val scope = rememberCoroutineScope()

    Box(Modifier.fillMaxSize().background(Background)) {
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
            val item = pages[page]
            Column(
                modifier = Modifier.fillMaxSize().padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(item.emoji, fontSize = 80.sp)
                Spacer(Modifier.height(32.dp))
                Text(item.title, style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    brush = Brush.linearGradient(listOf(item.accentColor, item.accentColor.copy(alpha = 0.7f)))
                ))
                Spacer(Modifier.height(16.dp))
                Text(item.subtitle, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
            }
        }

        // Page indicators + button
        Column(Modifier.align(Alignment.BottomCenter).padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(pages.size) { i ->
                    Box(Modifier.size(if (i == pagerState.currentPage) 24.dp else 8.dp, 8.dp)
                        .background(if (i == pagerState.currentPage) Primary else Primary.copy(alpha = 0.3f), MaterialTheme.shapes.extraLarge))
                }
            }
            Spacer(Modifier.height(24.dp))
            AppButton(
                text = if (pagerState.currentPage == pages.lastIndex) "Get Started" else "Next",
                onClick = {
                    if (pagerState.currentPage == pages.lastIndex) onFinish()
                    else scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                },
                modifier = Modifier.fillMaxWidth()
            )
            if (pagerState.currentPage < pages.lastIndex) {
                TextButton(onClick = onFinish) { Text("Skip", color = MaterialTheme.colorScheme.onSurfaceVariant) }
            }
        }
    }
}
