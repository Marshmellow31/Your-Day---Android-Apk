package com.yourday.app.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yourday.app.data.model.Subject
import com.yourday.app.ui.components.*
import com.yourday.app.ui.theme.*
import com.yourday.app.ui.viewmodel.AuthState
import com.yourday.app.ui.viewmodel.AuthViewModel
import com.yourday.app.ui.viewmodel.SubjectsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToTopics: (String, String) -> Unit,
    viewModel: SubjectsViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    val userId = (authState as? AuthState.LoggedIn)?.user?.uid ?: ""
    var showCreateDialog by remember { mutableStateOf(false) }
    var newSubjectName by remember { mutableStateOf("") }
    var newSubjectColorIndex by remember { mutableStateOf(0) }

    LaunchedEffect(userId) { if (userId.isNotEmpty()) viewModel.loadSubjects(userId) }

    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("New Subject") },
            text = {
                Column {
                    AppTextField(value = newSubjectName, onValueChange = { newSubjectName = it }, label = "Subject name")
                    Spacer(Modifier.height(12.dp))
                    Text("Color", style = MaterialTheme.typography.labelMedium, color = OnSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        subjectColors.take(6).forEachIndexed { i, color ->
                            Box(Modifier.size(28.dp).background(color, MaterialTheme.shapes.extraLarge)
                                .also { if (i == newSubjectColorIndex) it else it },
                                contentAlignment = Alignment.Center) {
                                RadioButton(selected = i == newSubjectColorIndex, onClick = { newSubjectColorIndex = i },
                                    colors = RadioButtonDefaults.colors(selectedColor = Color.White, unselectedColor = Color.Transparent))
                            }
                        }
                    }
                }
            },
            confirmButton = {
                AppButton("Create", onClick = {
                    if (newSubjectName.isNotBlank()) {
                        viewModel.createSubject(Subject(id = "", userId = userId, name = newSubjectName,
                            color = "#" + subjectColors[newSubjectColorIndex].value.toString(16).substring(2)))
                        newSubjectName = ""; showCreateDialog = false
                    }
                })
            },
            dismissButton = { TextButton(onClick = { showCreateDialog = false }) { Text("Cancel") } },
            containerColor = SurfaceContainer
        )
    }

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(title = { Text("Subjects", fontWeight = FontWeight.SemiBold) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background, titleContentColor = OnBackground))
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreateDialog = true }, containerColor = Primary) { Icon(Icons.Default.Add, null, tint = OnPrimary) }
        }
    ) { padding ->
        when {
            uiState.isLoading -> LoadingScreen()
            uiState.subjects.isEmpty() -> EmptyState("No subjects yet", "Add your first subject to get started", "📚")
            else -> LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.subjects, key = { it.id }) { subject ->
                    val accent = try { Color(android.graphics.Color.parseColor(subject.color)) } catch (e: Exception) { Primary }
                    AppCard(modifier = Modifier.fillMaxWidth().height(100.dp), onClick = { onNavigateToTopics(subject.id, subject.name) }) {
                        Box(Modifier.size(12.dp).background(accent, MaterialTheme.shapes.extraSmall))
                        Spacer(Modifier.height(8.dp))
                        Text(subject.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, maxLines = 2)
                    }
                }
            }
        }
    }
}
