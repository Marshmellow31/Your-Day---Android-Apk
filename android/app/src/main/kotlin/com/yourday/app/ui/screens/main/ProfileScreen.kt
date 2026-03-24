package com.yourday.app.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import coil.compose.AsyncImage
import com.yourday.app.ui.components.*
import com.yourday.app.ui.theme.*
import com.yourday.app.ui.viewmodel.AuthState
import com.yourday.app.ui.viewmodel.AuthViewModel
import com.yourday.app.ui.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    val userId = (authState as? AuthState.LoggedIn)?.user?.uid ?: ""

    LaunchedEffect(userId) { if (userId.isNotEmpty()) viewModel.loadProfile(userId) }

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(title = { Text("Profile", fontWeight = FontWeight.SemiBold) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, null) } },
                actions = { IconButton(onClick = onNavigateToSettings) { Icon(Icons.Default.Settings, null, tint = OnSurfaceVariant) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background, titleContentColor = OnBackground))
        }
    ) { padding ->
        if (uiState.isLoading) { LoadingScreen(); return@Scaffold }

        LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item {
                // Avatar & name
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        Modifier.size(88.dp).background(Brush.radialGradient(listOf(Primary, PrimaryDark)), MaterialTheme.shapes.extraLarge),
                        contentAlignment = Alignment.Center
                    ) {
                        if (uiState.user?.photoUrl != null) {
                            AsyncImage(model = uiState.user?.photoUrl, contentDescription = null, modifier = Modifier.fillMaxSize())
                        } else {
                            Text(uiState.user?.displayName?.firstOrNull()?.toString() ?: "U", style = MaterialTheme.typography.headlineLarge, color = OnPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(uiState.user?.displayName ?: "", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(uiState.user?.email ?: "", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
                }
            }
            item {
                AppCard {
                    ProfileRow(Icons.Default.Email, "Email", uiState.user?.email ?: "")
                    Divider(color = OutlineVariant, modifier = Modifier.padding(vertical = 8.dp))
                    ProfileRow(Icons.Default.CalendarToday, "Study Goals", uiState.user?.studyGoals?.ifEmpty { "Not set" } ?: "Not set")
                }
            }
            item {
                AppButton(text = "Sign Out", onClick = { viewModel.signOut(); onLogout() }, modifier = Modifier.fillMaxWidth(), variant = ButtonVariant.OUTLINED)
            }
        }
    }
}

@Composable
private fun ProfileRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = OnSurfaceVariant, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(12.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
            Text(value, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
