package com.yourday.app.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yourday.app.ui.components.*
import com.yourday.app.ui.theme.*
import com.yourday.app.ui.viewmodel.AnalyticsViewModel
import com.yourday.app.ui.viewmodel.AuthState
import com.yourday.app.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    onNavigateBack: () -> Unit,
    viewModel: AnalyticsViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    val userId = (authState as? AuthState.LoggedIn)?.user?.uid ?: ""

    LaunchedEffect(userId) { if (userId.isNotEmpty()) viewModel.loadAnalytics(userId) }

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(title = { Text("Analytics", fontWeight = FontWeight.SemiBold) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background, titleContentColor = OnBackground))
        }
    ) { padding ->
        if (uiState.isLoading) { LoadingScreen(); return@Scaffold }
        LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Summary cards
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AppCard(modifier = Modifier.weight(1f)) {
                        Text("${(uiState.completionRate * 100).toInt()}%", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Primary)
                        Text("Completion Rate", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                    }
                    AppCard(modifier = Modifier.weight(1f)) {
                        Text("${uiState.completedThisWeek}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Secondary)
                        Text("Done This Week", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                    }
                }
            }

            // Progress bar
            item {
                AppCard {
                    Text("Weekly Progress", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = { uiState.completionRate },
                        modifier = Modifier.fillMaxWidth().height(8.dp),
                        color = Primary,
                        trackColor = SurfaceContainerHigh
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("${uiState.completedThisWeek} of ${uiState.totalTasksThisWeek} tasks completed", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                }
            }

            // Daily bar chart (simplified)
            item {
                AppCard {
                    Text("Daily Completions", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(16.dp))
                    val maxVal = (uiState.dailyCompletions.maxOfOrNull { it.second } ?: 1).coerceAtLeast(1)
                    Row(Modifier.fillMaxWidth().height(80.dp), verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.SpaceEvenly) {
                        uiState.dailyCompletions.forEach { (day, count) ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                Box(Modifier.weight(1f), contentAlignment = Alignment.BottomCenter) {
                                    Box(
                                        Modifier
                                            .fillMaxWidth(0.6f)
                                            .fillMaxHeight(if (count == 0) 0.04f else count.toFloat() / maxVal)
                                            .background(Brush.verticalGradient(listOf(PrimaryLight, Primary)), MaterialTheme.shapes.extraSmall)
                                    )
                                }
                                Spacer(Modifier.height(4.dp))
                                Text(day, style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
