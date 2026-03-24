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
import com.yourday.app.ui.components.EmptyState
import com.yourday.app.ui.components.AppCard
import com.yourday.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicsScreen(
    subjectId: String,
    subjectName: String,
    onNavigateBack: () -> Unit,
    onCreateTask: () -> Unit
) {
    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(title = { Text(subjectName, fontWeight = FontWeight.SemiBold) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background, titleContentColor = OnBackground))
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateTask, containerColor = Primary) { Icon(Icons.Default.Add, null, tint = OnPrimary) }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            EmptyState("No topics yet", "Topics for $subjectName will appear here. Coming soon!", "📝")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchedulerScreen(onNavigateBack: () -> Unit) {
    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(title = { Text("Smart Scheduler", fontWeight = FontWeight.SemiBold) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background, titleContentColor = OnBackground))
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            EmptyState("Scheduler", "The smart scheduler lets you auto-allocate tasks to your free study blocks. Full implementation coming soon!", "🗓️")
        }
    }
}
