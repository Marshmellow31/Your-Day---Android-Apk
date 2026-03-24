package com.yourday.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourday.app.data.model.Subject
import com.yourday.app.data.model.Task
import com.yourday.app.data.repository.SubjectRepository
import com.yourday.app.data.repository.TaskRepository
import com.yourday.app.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = true,
    val greeting: String = "",
    val userName: String = "",
    val todayTasks: List<Task> = emptyList(),
    val pendingCount: Int = 0,
    val completedTodayCount: Int = 0,
    val streak: Int = 0,
    val subjects: List<Subject> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val subjectRepository: SubjectRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun loadData(userId: String, userName: String) {
        _uiState.update { it.copy(greeting = getGreeting(), userName = userName) }

        viewModelScope.launch {
            combine(
                taskRepository.getTasksFlow(userId),
                subjectRepository.getSubjectsFlow(userId)
            ) { tasks, subjects -> Pair(tasks, subjects) }
                .catch { error -> _uiState.update { it.copy(isLoading = false, error = error.message) } }
                .collect { (tasks, subjects) ->
                    val todayStart = getTodayStart()
                    val todayEnd = todayStart + 86_400_000L

                    val todayTasks = tasks.filter { task ->
                        val inRange = task.dueDate?.let { it in todayStart..todayEnd } ?: false
                        val isPending = !task.isCompleted
                        inRange || isPending
                    }.sortedWith(compareBy({ it.isCompleted }, { it.dueDate ?: Long.MAX_VALUE }))

                    val completedToday = tasks.count { task ->
                        task.isCompleted && task.completedAt?.let { it in todayStart..todayEnd } == true
                    }

                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            todayTasks = todayTasks.take(10),
                            pendingCount = tasks.count { !it.isCompleted },
                            completedTodayCount = completedToday,
                            subjects = subjects,
                            error = null
                        )
                    }
                }
        }
    }

    private fun getGreeting(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when {
            hour < 12 -> "Good morning"
            hour < 17 -> "Good afternoon"
            else -> "Good evening"
        }
    }

    private fun getTodayStart(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}
