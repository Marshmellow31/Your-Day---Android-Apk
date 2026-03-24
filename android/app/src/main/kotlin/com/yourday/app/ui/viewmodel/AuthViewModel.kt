package com.yourday.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourday.app.data.model.User
import com.yourday.app.data.repository.AuthRepository
import com.yourday.app.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthState {
    object Loading : AuthState()
    data class LoggedIn(val user: User) : AuthState()
    object LoggedOut : AuthState()
    data class Error(val message: String) : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        checkAuthState()
    }

    private fun checkAuthState() {
        viewModelScope.launch {
            val uid = authRepository.getCurrentUserId()
            if (uid != null) {
                val userResult = userRepository.getUser(uid)
                val user = userResult.getOrNull()
                if (user != null) {
                    _authState.value = AuthState.LoggedIn(user)
                } else {
                    _authState.value = AuthState.LoggedOut
                }
            } else {
                _authState.value = AuthState.LoggedOut
            }
        }
    }

    fun signInWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val result = authRepository.signInWithEmail(email.trim(), password)
            result.onSuccess { user ->
                _authState.value = AuthState.LoggedIn(user)
            }.onFailure { error ->
                _errorMessage.value = error.localizedMessage ?: "Sign in failed"
                _authState.value = AuthState.LoggedOut
            }
            _isLoading.value = false
        }
    }

    fun registerWithEmail(email: String, password: String, displayName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val result = authRepository.registerWithEmail(email.trim(), password, displayName.trim())
            result.onSuccess { user ->
                _authState.value = AuthState.LoggedIn(user)
            }.onFailure { error ->
                _errorMessage.value = error.localizedMessage ?: "Registration failed"
            }
            _isLoading.value = false
        }
    }

    fun sendPasswordReset(email: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            authRepository.sendPasswordReset(email.trim())
                .onSuccess { onSuccess() }
                .onFailure { _errorMessage.value = it.localizedMessage ?: "Failed to send reset email" }
            _isLoading.value = false
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            _authState.value = AuthState.LoggedOut
        }
    }

    fun clearError() { _errorMessage.value = null }
}
