package com.yourday.app.ui.screens.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yourday.app.ui.components.AppButton
import com.yourday.app.ui.components.AppTextField
import com.yourday.app.ui.theme.Background
import com.yourday.app.ui.theme.OnBackground
import com.yourday.app.ui.viewmodel.AuthState
import com.yourday.app.ui.viewmodel.AuthViewModel
import com.yourday.app.ui.viewmodel.TasksViewModel
import com.yourday.app.data.model.Task
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEditTaskScreen(
    taskId: String?,
    onNavigateBack: () -> Unit,
    viewModel: TasksViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    val userId = (authState as? AuthState.LoggedIn)?.user?.uid ?: ""
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("medium") }
    val isEditing = taskId != null

    LaunchedEffect(userId) { if (userId.isNotEmpty()) viewModel.loadTasks(userId) }

    LaunchedEffect(taskId, uiState.tasks) {
        if (taskId != null) {
            val task = uiState.tasks.find { it.id == taskId }
            task?.let { title = it.title; description = it.description; priority = it.priority }
        }
    }

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Edit Task" else "New Task", fontWeight = FontWeight.SemiBold) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background, titleContentColor = OnBackground)
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 24.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Spacer(Modifier.height(8.dp))
            AppTextField(value = title, onValueChange = { title = it }, label = "Task title", leadingIcon = Icons.Default.Title, maxLines = 2)
            AppTextField(value = description, onValueChange = { description = it }, label = "Description (optional)", leadingIcon = Icons.AutoMirrored.Filled.Notes, maxLines = 4)

            // Priority selection
            Text("Priority", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("low", "medium", "high").forEach { p ->
                    FilterChip(selected = priority == p, onClick = { priority = p }, label = { Text(p.replaceFirstChar { it.uppercase() }) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = when (p) { "high" -> com.yourday.app.ui.theme.PriorityHigh.copy(alpha = 0.2f); "low" -> com.yourday.app.ui.theme.PriorityLow.copy(alpha = 0.2f); else -> com.yourday.app.ui.theme.PriorityMedium.copy(alpha = 0.2f) },
                            selectedLabelColor = when (p) { "high" -> com.yourday.app.ui.theme.PriorityHigh; "low" -> com.yourday.app.ui.theme.PriorityLow; else -> com.yourday.app.ui.theme.PriorityMedium }
                        ))
                }
            }

            Spacer(Modifier.height(8.dp))
            AppButton(
                text = if (isEditing) "Update Task" else "Create Task",
                onClick = {
                    if (title.isNotBlank()) {
                        if (taskId != null) {
                            viewModel.updateTask(taskId, mapOf("title" to title, "description" to description, "priority" to priority))
                        } else {
                            viewModel.createTask(Task(id = UUID.randomUUID().toString(), userId = userId, title = title, description = description, priority = priority))
                        }
                        onNavigateBack()
                    }
                },
                enabled = title.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
