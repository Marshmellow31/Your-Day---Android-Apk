package com.yourday.app.data.repository

import com.yourday.app.data.model.AppNotification
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    fun getNotificationsFlow(userId: String): Flow<List<AppNotification>>
    fun getUnreadCountFlow(userId: String): Flow<Int>
    suspend fun markRead(id: String): Result<Unit>
    suspend fun markAllRead(userId: String): Result<Unit>
    suspend fun syncFromFirestore(userId: String): Result<Unit>
    suspend fun saveLocalNotification(notification: AppNotification)
}
