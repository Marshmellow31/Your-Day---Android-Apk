package com.yourday.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourday.app.data.model.Task
import com.yourday.app.data.repository.SubjectRepository
import com.yourday.app.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class TaskFilter { ALL, PENDING, COMPLETED }

data class TasksUiState(
    val isLoading: Boolean = true,
    val tasks: List<Task> = emptyList(),
    val filter: TaskFilter = TaskFilter.PENDING,
    val error: String? = null,
    val isCreating: Boolean = false
)

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val subjectRepository: SubjectRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TasksUiState())
    val uiState: StateFlow<TasksUiState> = _uiState.asStateFlow()

    private var currentUserId: String = ""

    fun loadTasks(userId: String) {
        currentUserId = userId
        viewModelScope.launch {
            taskRepository.getTasksFlow(userId)
                .catch { error -> _uiState.update { it.copy(isLoading = false, error = error.message) } }
                .collect { tasks ->
                    val filtered = applyFilter(tasks, _uiState.value.filter)
                    _uiState.update { it.copy(isLoading = false, tasks = filtered) }
                }
        }
    }

    fun setFilter(filter: TaskFilter) {
        _uiState.update { it.copy(filter = filter) }
        viewModelScope.launch {
            taskRepository.getTasksFlow(currentUserId)
                .firstOrNull()
                ?.let { tasks ->
                    _uiState.update { it.copy(tasks = applyFilter(tasks, filter)) }
                }
        }
    }

    fun createTask(task: Task) {
        viewModelScope.launch {
            _uiState.update { it.copy(isCreating = true) }
            taskRepository.createTask(task)
                .onFailure { error -> _uiState.update { it.copy(error = error.message) } }
            _uiState.update { it.copy(isCreating = false) }
        }
    }

    fun updateTask(taskId: String, data: Map<String, Any?>) {
        viewModelScope.launch {
            taskRepository.updateTask(taskId, data)
                .onFailure { error -> _uiState.update { it.copy(error = error.message) } }
        }
    }

    fun completeTask(taskId: String) {
        viewModelScope.launch {
            taskRepository.completeTask(taskId)
                .onFailure { error -> _uiState.update { it.copy(error = error.message) } }
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            taskRepository.deleteTask(taskId, currentUserId)
                .onFailure { error -> _uiState.update { it.copy(error = error.message) } }
        }
    }

    private fun applyFilter(tasks: List<Task>, filter: TaskFilter) = when (filter) {
        TaskFilter.ALL -> tasks
        TaskFilter.PENDING -> tasks.filter { !it.isCompleted }
        TaskFilter.COMPLETED -> tasks.filter { it.isCompleted }
    }

    fun clearError() { _uiState.update { it.copy(error = null) } }
}
