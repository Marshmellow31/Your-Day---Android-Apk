package com.yourday.app.ui.screens.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.yourday.app.ui.viewmodel.AuthViewModel
import com.yourday.app.ui.viewmodel.HomeViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToTasks: () -> Unit,
    onNavigateToSubjects: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToScheduler: () -> Unit,
    onNavigateToGoals: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onCreateTask: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val authState by authViewModel.authState.collectAsStateWithLifecycle()

    val userId = (authState as? com.yourday.app.ui.viewmodel.AuthState.LoggedIn)?.user?.uid ?: ""
    val userName = (authState as? com.yourday.app.ui.viewmodel.AuthState.LoggedIn)?.user?.displayName ?: ""

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) viewModel.loadData(userId, userName)
    }

    Scaffold(
        containerColor = Background,
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateTask, containerColor = Primary) {
                Icon(Icons.Default.Add, "Add Task", tint = OnPrimary)
            }
        }
    ) { padding ->
        when {
            uiState.isLoading -> LoadingScreen()
            uiState.error != null -> ErrorState(uiState.error ?: "") { viewModel.loadData(userId, userName) }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    // Header
                    item {
                        Box(
                            Modifier.fillMaxWidth().background(
                                Brush.verticalGradient(listOf(SurfaceContainer, Background))
                            ).padding(horizontal = 20.dp, vertical = 24.dp)
                        ) {
                            Column {
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Column {
                                        Text("${uiState.greeting},", style = MaterialTheme.typography.bodyLarge, color = OnSurfaceVariant)
                                        Text(
                                            text = userName.ifEmpty { "Student" },
                                            style = MaterialTheme.typography.headlineMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                brush = Brush.linearGradient(listOf(Primary, Secondary))
                                            )
                                        )
                                    }
                                    Row {
                                        IconButton(onClick = onNavigateToNotifications) { Icon(Icons.Default.Notifications, null, tint = OnSurfaceVariant) }
                                        IconButton(onClick = onNavigateToProfile) { Icon(Icons.Default.AccountCircle, null, tint = OnSurfaceVariant) }
                                    }
                                }
                                Spacer(Modifier.height(8.dp))
                                Text(SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date()), style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
                            }
                        }
                    }

                    // Stats Row
                    item {
                        Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            StatCard("📋", "${uiState.pendingCount}", "Pending", Modifier.weight(1f))
                            StatCard("✅", "${uiState.completedTodayCount}", "Done Today", Modifier.weight(1f))
                            StatCard("📚", "${uiState.subjects.size}", "Subjects", Modifier.weight(1f), onClick = onNavigateToSubjects)
                        }
                    }

                    // Quick Actions
                    item {
                        Text("Quick Access", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp))
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(listOf(
                                Triple("📊", "Analytics", onNavigateToAnalytics),
                                Triple("🗓️", "Scheduler", onNavigateToScheduler),
                                Triple("🎯", "Goals", onNavigateToGoals),
                                Triple("⚙️", "Settings", onNavigateToSettings)
                            )) { (emoji, label, action) ->
                                QuickActionChip(emoji, label, onClick = action)
                            }
                        }
                    }

                    // Today's Tasks
                    item {
                        Row(
                            Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(top = 20.dp, bottom = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Today's Tasks", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            TextButton(onClick = onNavigateToTasks) { Text("See all", color = Primary) }
                        }
                    }

                    if (uiState.todayTasks.isEmpty()) {
                        item { EmptyState("No tasks yet", "Tap + to create your first task", "🚀") }
                    } else {
                        items(uiState.todayTasks) { task ->
                            TaskListItem(task = task, modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(emoji: String, value: String, label: String, modifier: Modifier = Modifier, onClick: (() -> Unit)? = null) {
    AppCard(modifier = modifier, onClick = onClick) {
        Text(emoji, fontSize = 20.sp)
        Spacer(Modifier.height(4.dp))
        Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Primary)
        Text(label, style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
    }
}

@Composable
private fun QuickActionChip(emoji: String, label: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        color = SurfaceContainerHigh,
        modifier = Modifier.height(72.dp).width(88.dp)
    ) {
        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(emoji, fontSize = 22.sp)
            Spacer(Modifier.height(4.dp))
            Text(label, style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
        }
    }
}

@Composable
private fun TaskListItem(task: com.yourday.app.data.model.Task, modifier: Modifier = Modifier) {
    val priorityColor = when (task.priority) {
        "high" -> PriorityHigh
        "low" -> PriorityLow
        else -> PriorityMedium
    }
    AppCard(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(4.dp, 40.dp).background(priorityColor, MaterialTheme.shapes.extraSmall))
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(task.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium,
                    color = if (task.isCompleted) OnSurfaceVariant else OnSurface,
                    textDecoration = if (task.isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null)
                if (task.description.isNotEmpty()) {
                    Text(task.description, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant, maxLines = 1)
                }
            }
            if (task.isCompleted) Icon(Icons.Default.CheckCircle, null, tint = Success, modifier = Modifier.size(20.dp))
        }
    }
}
