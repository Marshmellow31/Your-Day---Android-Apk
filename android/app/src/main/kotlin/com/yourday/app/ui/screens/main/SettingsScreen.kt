package com.yourday.app.ui.screens.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yourday.app.ui.components.AppCard
import com.yourday.app.ui.theme.*
import com.yourday.app.ui.viewmodel.AuthState
import com.yourday.app.ui.viewmodel.AuthViewModel
import com.yourday.app.ui.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    val uiState by profileViewModel.uiState.collectAsStateWithLifecycle()
    val userId = (authState as? AuthState.LoggedIn)?.user?.uid ?: ""

    LaunchedEffect(userId) { if (userId.isNotEmpty()) profileViewModel.loadProfile(userId) }

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(title = { Text("Settings", fontWeight = FontWeight.SemiBold) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background, titleContentColor = OnBackground))
        }
    ) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Text("Preferences", style = MaterialTheme.typography.labelMedium, color = OnSurfaceVariant, modifier = Modifier.padding(vertical = 4.dp))
                AppCard {
                    SettingsRow(Icons.Default.Notifications, "Push Notifications",
                        trailing = {
                            Switch(checked = uiState.user?.notificationEnabled ?: false, onCheckedChange = { enabled ->
                                profileViewModel.updateProfile(userId, mapOf("notificationEnabled" to enabled))
                            }, colors = SwitchDefaults.colors(checkedThumbColor = OnPrimary, checkedTrackColor = Primary))
                        })
                    Divider(color = OutlineVariant, modifier = Modifier.padding(vertical = 8.dp))
                    SettingsRow(Icons.Default.Palette, "Theme", valueText = "Dark")
                    Divider(color = OutlineVariant, modifier = Modifier.padding(vertical = 8.dp))
                    SettingsRow(Icons.Default.CalendarMonth, "Week Start", valueText = uiState.user?.weekStartDay?.replaceFirstChar { it.uppercase() } ?: "Monday")
                }
            }
            item {
                Text("Reminders", style = MaterialTheme.typography.labelMedium, color = OnSurfaceVariant, modifier = Modifier.padding(vertical = 4.dp))
                AppCard {
                    SettingsRow(Icons.Default.Timer, "Default Reminder", valueText = "${uiState.user?.defaultMinutesBefore ?: 30} min before")
                }
            }
            item {
                Text("About", style = MaterialTheme.typography.labelMedium, color = OnSurfaceVariant, modifier = Modifier.padding(vertical = 4.dp))
                AppCard {
                    SettingsRow(Icons.Default.Info, "Version", valueText = "1.0.0")
                }
            }
        }
    }
}

@Composable
private fun SettingsRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, valueText: String? = null, trailing: @Composable (() -> Unit)? = null) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Row {
            Icon(icon, null, tint = OnSurfaceVariant, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(12.dp))
            Text(label, style = MaterialTheme.typography.bodyMedium)
        }
        if (trailing != null) trailing()
        else if (valueText != null) Text(valueText, style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
    }
}
