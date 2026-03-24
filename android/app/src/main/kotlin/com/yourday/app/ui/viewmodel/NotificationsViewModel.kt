package com.yourday.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourday.app.data.model.AppNotification
import com.yourday.app.data.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NotificationsUiState(
    val isLoading: Boolean = true,
    val notifications: List<AppNotification> = emptyList(),
    val unreadCount: Int = 0,
    val error: String? = null
)

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    fun loadNotifications(userId: String) {
        viewModelScope.launch {
            combine(
                notificationRepository.getNotificationsFlow(userId),
                notificationRepository.getUnreadCountFlow(userId)
            ) { notifs, count -> Pair(notifs, count) }
                .catch { error -> _uiState.update { it.copy(isLoading = false, error = error.message) } }
                .collect { (notifs, count) ->
                    _uiState.update { it.copy(isLoading = false, notifications = notifs, unreadCount = count) }
                }
        }
        viewModelScope.launch {
            notificationRepository.syncFromFirestore(userId)
        }
    }

    fun markRead(id: String) {
        viewModelScope.launch { notificationRepository.markRead(id) }
    }

    fun markAllRead(userId: String) {
        viewModelScope.launch { notificationRepository.markAllRead(userId) }
    }
}
