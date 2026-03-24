package com.yourday.app.ui.screens.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yourday.app.ui.components.*
import com.yourday.app.ui.theme.*
import com.yourday.app.ui.viewmodel.AuthState
import com.yourday.app.ui.viewmodel.AuthViewModel
import com.yourday.app.ui.viewmodel.GoalsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen(
    onNavigateBack: () -> Unit,
    viewModel: GoalsViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    val userId = (authState as? AuthState.LoggedIn)?.user?.uid ?: ""

    LaunchedEffect(userId) { if (userId.isNotEmpty()) viewModel.loadGoals(userId) }

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(title = { Text("Personal Goals", fontWeight = FontWeight.SemiBold) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background, titleContentColor = OnBackground))
        }
    ) { padding ->
        when {
            uiState.isLoading -> LoadingScreen()
            uiState.goals.isEmpty() -> EmptyState("No goals yet", "Set your first goal and track progress", "🎯")
            else -> LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(uiState.goals, key = { it.id }) { goal ->
                    AppCard(modifier = Modifier.fillMaxWidth()) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column(Modifier.weight(1f)) {
                                Text(goal.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                                Text("${goal.totalProgress} / ${goal.totalTarget} ${goal.unit}", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                            }
                            Text("${(goal.progressPercent * 100).toInt()}%", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Primary)
                        }
                        Spacer(Modifier.height(12.dp))
                        LinearProgressIndicator(
                            progress = { goal.progressPercent },
                            modifier = Modifier.fillMaxWidth().height(6.dp),
                            color = Primary,
                            trackColor = SurfaceContainerHigh
                        )
                        Spacer(Modifier.height(8.dp))
                        Text("${goal.durationDays} day goal · ${goal.category}", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                    }
                }
            }
        }
    }
}
