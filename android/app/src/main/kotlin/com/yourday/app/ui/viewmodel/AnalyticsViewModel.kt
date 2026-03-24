package com.yourday.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourday.app.data.model.Task
import com.yourday.app.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class AnalyticsUiState(
    val isLoading: Boolean = true,
    val totalTasksThisWeek: Int = 0,
    val completedThisWeek: Int = 0,
    val completionRate: Float = 0f,
    val streakDays: Int = 0,
    val dailyCompletions: List<Pair<String, Int>> = emptyList(), // day label to count
    val subjectBreakdown: Map<String, Int> = emptyMap(),
    val error: String? = null
)

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    fun loadAnalytics(userId: String) {
        viewModelScope.launch {
            taskRepository.getTasksFlow(userId)
                .catch { error -> _uiState.update { it.copy(isLoading = false, error = error.message) } }
                .collect { tasks ->
                    val weekStart = getWeekStart()
                    val weekEnd = weekStart + 7 * 86_400_000L
                    val weekTasks = tasks.filter { it.createdAt in weekStart..weekEnd || (it.dueDate?.let { d -> d in weekStart..weekEnd } == true) }
                    val completedWeek = weekTasks.filter { it.isCompleted }
                    val rate = if (weekTasks.isNotEmpty()) completedWeek.size.toFloat() / weekTasks.size else 0f

                    val dailyMap = mutableMapOf<String, Int>()
                    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                    days.forEach { dailyMap[it] = 0 }
                    completedWeek.forEach { task ->
                        task.completedAt?.let { ts ->
                            val cal = Calendar.getInstance().apply { timeInMillis = ts }
                            val dayIndex = (cal.get(Calendar.DAY_OF_WEEK) + 5) % 7
                            val key = days.getOrElse(dayIndex) { "Mon" }
                            dailyMap[key] = (dailyMap[key] ?: 0) + 1
                        }
                    }

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            totalTasksThisWeek = weekTasks.size,
                            completedThisWeek = completedWeek.size,
                            completionRate = rate,
                            dailyCompletions = days.map { d -> Pair(d, dailyMap[d] ?: 0) },
                        )
                    }
                }
        }
    }

    private fun getWeekStart(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}
