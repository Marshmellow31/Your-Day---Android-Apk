package com.yourday.app.data.repository

import com.yourday.app.data.local.dao.NotificationDao
import com.yourday.app.data.model.AppNotification
import com.yourday.app.data.model.toEntity
import com.yourday.app.data.model.toNotification
import com.yourday.app.data.remote.NotificationDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val notificationDao: NotificationDao,
    private val notificationDataSource: NotificationDataSource
) : NotificationRepository {

    override fun getNotificationsFlow(userId: String): Flow<List<AppNotification>> =
        notificationDao.getNotificationsFlow(userId).map { it.map { e -> e.toNotification() } }

    override fun getUnreadCountFlow(userId: String): Flow<Int> =
        notificationDao.getUnreadCountFlow(userId)

    override suspend fun markRead(id: String): Result<Unit> = runCatching {
        notificationDao.markAsRead(id)
        notificationDataSource.markRead(id)
    }

    override suspend fun markAllRead(userId: String): Result<Unit> = runCatching {
        notificationDao.markAllAsRead(userId)
    }

    override suspend fun syncFromFirestore(userId: String): Result<Unit> = runCatching {
        val notifications = notificationDataSource.getNotifications(userId).getOrThrow()
        notificationDao.deleteAllForUser(userId)
        notifications.forEach { notificationDao.insertNotification(it.toEntity()) }
    }

    override suspend fun saveLocalNotification(notification: AppNotification) {
        notificationDao.insertNotification(notification.toEntity())
    }
}
