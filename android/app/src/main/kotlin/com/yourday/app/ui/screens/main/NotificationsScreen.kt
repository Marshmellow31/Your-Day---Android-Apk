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
import com.yourday.app.ui.viewmodel.NotificationsViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onNavigateBack: () -> Unit,
    viewModel: NotificationsViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    val userId = (authState as? AuthState.LoggedIn)?.user?.uid ?: ""

    LaunchedEffect(userId) { if (userId.isNotEmpty()) viewModel.loadNotifications(userId) }

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = { Text("Notifications", fontWeight = FontWeight.SemiBold) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, null) } },
                actions = {
                    if (uiState.unreadCount > 0) {
                        TextButton(onClick = { viewModel.markAllRead(userId) }) { Text("Mark all read", color = Primary) }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background, titleContentColor = OnBackground)
            )
        }
    ) { padding ->
        when {
            uiState.isLoading -> LoadingScreen()
            uiState.notifications.isEmpty() -> Column(Modifier.fillMaxSize().padding(padding)) {
                EmptyState("No notifications yet", "You're all caught up!", "🔔")
            }
            else -> LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(uiState.notifications, key = { it.id }) { notification ->
                    AppCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { if (!notification.isRead) viewModel.markRead(notification.id) }
                    ) {
                        Row(verticalAlignment = Alignment.Top) {
                            Box(Modifier.size(8.dp).align(Alignment.Top).offset(y = 4.dp)
                                .background(if (!notification.isRead) Primary else androidx.compose.ui.graphics.Color.Transparent, MaterialTheme.shapes.extraSmall))
                            Spacer(Modifier.width(8.dp))
                            Column(Modifier.weight(1f)) {
                                Text(notification.title, style = MaterialTheme.typography.bodyMedium, fontWeight = if (!notification.isRead) FontWeight.SemiBold else FontWeight.Normal)
                                Spacer(Modifier.height(4.dp))
                                Text(notification.body, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                                Spacer(Modifier.height(4.dp))
                                Text(SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date(notification.createdAt)), style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }
}
