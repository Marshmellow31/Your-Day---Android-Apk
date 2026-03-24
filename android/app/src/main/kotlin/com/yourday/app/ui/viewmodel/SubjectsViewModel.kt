package com.yourday.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourday.app.data.model.Subject
import com.yourday.app.data.repository.SubjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SubjectsUiState(
    val isLoading: Boolean = true,
    val subjects: List<Subject> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class SubjectsViewModel @Inject constructor(
    private val subjectRepository: SubjectRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SubjectsUiState())
    val uiState: StateFlow<SubjectsUiState> = _uiState.asStateFlow()

    private var currentUserId: String = ""

    fun loadSubjects(userId: String) {
        currentUserId = userId
        viewModelScope.launch {
            subjectRepository.getSubjectsFlow(userId)
                .catch { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
                .collect { subjects -> _uiState.update { it.copy(isLoading = false, subjects = subjects) } }
        }
        viewModelScope.launch { subjectRepository.syncFromFirestore(userId) }
    }

    fun createSubject(subject: Subject) {
        viewModelScope.launch {
            subjectRepository.createSubject(subject)
                .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
        }
    }

    fun deleteSubject(subjectId: String) {
        viewModelScope.launch {
            subjectRepository.deleteSubject(subjectId, currentUserId)
                .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
        }
    }

    fun clearError() { _uiState.update { it.copy(error = null) } }
}
