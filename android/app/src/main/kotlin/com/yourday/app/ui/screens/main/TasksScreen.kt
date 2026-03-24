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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yourday.app.ui.components.*
import com.yourday.app.ui.theme.*
import com.yourday.app.ui.viewmodel.AuthState
import com.yourday.app.ui.viewmodel.AuthViewModel
import com.yourday.app.ui.viewmodel.TaskFilter
import com.yourday.app.ui.viewmodel.TasksViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    onNavigateBack: () -> Unit,
    onCreateTask: () -> Unit,
    onEditTask: (String) -> Unit,
    viewModel: TasksViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    val userId = (authState as? AuthState.LoggedIn)?.user?.uid ?: ""

    LaunchedEffect(userId) { if (userId.isNotEmpty()) viewModel.loadTasks(userId) }

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(title = { Text("Tasks", fontWeight = FontWeight.SemiBold) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background, titleContentColor = OnBackground))
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateTask, containerColor = Primary) { Icon(Icons.Default.Add, null, tint = OnPrimary) }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            // Filter chips
            Row(Modifier.padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TaskFilter.values().forEach { filter ->
                    FilterChip(selected = uiState.filter == filter, onClick = { viewModel.setFilter(filter) },
                        label = { Text(filter.name.lowercase().replaceFirstChar { it.uppercase() }) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = PrimaryContainer, selectedLabelColor = OnPrimaryContainer))
                }
            }
            Spacer(Modifier.height(8.dp))
            when {
                uiState.isLoading -> LoadingScreen()
                uiState.tasks.isEmpty() -> EmptyState("No tasks", "Create your first task with the + button", "📋")
                else -> LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(uiState.tasks, key = { it.id }) { task ->
                        SwipeableTaskCard(task = task, onComplete = { viewModel.completeTask(task.id) }, onEdit = { onEditTask(task.id) }, onDelete = { viewModel.deleteTask(task.id) })
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeableTaskCard(
    task: com.yourday.app.data.model.Task,
    onComplete: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            when (value) {
                SwipeToDismissBoxValue.EndToStart -> { onDelete(); true }
                SwipeToDismissBoxValue.StartToEnd -> { onComplete(); false }
                else -> false
            }
        }
    )
    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val color = if (dismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd) Success else Error
            Box(Modifier.fillMaxSize().background(color, MaterialTheme.shapes.medium).padding(horizontal = 20.dp)) {
                Icon(if (dismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd) Icons.Default.Check else Icons.Default.Delete, null,
                    tint = OnPrimary, modifier = Modifier.align(if (dismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd) Alignment.CenterStart else Alignment.CenterEnd))
            }
        },
        content = {
            AppCard(modifier = Modifier.fillMaxWidth(), onClick = onEdit) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val priorityColor = when (task.priority) { "high" -> PriorityHigh; "low" -> PriorityLow; else -> PriorityMedium }
                    Box(Modifier.size(4.dp, 44.dp).background(priorityColor, MaterialTheme.shapes.extraSmall))
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(task.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = if (task.isCompleted) OnSurfaceVariant else OnSurface)
                        if (task.dueDate != null) {
                            Text(SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date(task.dueDate)), style = MaterialTheme.typography.bodySmall, color = if (task.isOverdue) Error else OnSurfaceVariant)
                        }
                    }
                    if (task.isCompleted) Icon(Icons.Default.CheckCircle, null, tint = Success)
                    else Icon(Icons.Default.RadioButtonUnchecked, null, tint = OnSurfaceVariant, modifier = Modifier.size(20.dp))
                }
            }
        }
    )
}
