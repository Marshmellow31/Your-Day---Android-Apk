package com.yourday.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourday.app.data.model.PersonalGoal
import com.yourday.app.data.repository.GoalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GoalsUiState(
    val isLoading: Boolean = true,
    val goals: List<PersonalGoal> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class GoalsViewModel @Inject constructor(
    private val goalRepository: GoalRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GoalsUiState())
    val uiState: StateFlow<GoalsUiState> = _uiState.asStateFlow()

    private var currentUserId: String = ""

    fun loadGoals(userId: String) {
        currentUserId = userId
        viewModelScope.launch {
            goalRepository.getGoalsFlow(userId)
                .catch { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
                .collect { goals -> _uiState.update { it.copy(isLoading = false, goals = goals) } }
        }
        viewModelScope.launch { goalRepository.syncFromFirestore(userId) }
    }

    fun createGoal(goal: PersonalGoal) {
        viewModelScope.launch {
            goalRepository.createGoal(goal)
                .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
        }
    }

    fun deleteGoal(goalId: String) {
        viewModelScope.launch {
            goalRepository.deleteGoal(goalId, currentUserId)
                .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
        }
    }

    fun clearError() { _uiState.update { it.copy(error = null) } }
}
