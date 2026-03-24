package com.yourday.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val body: String,
    val type: String = "reminder",
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

data class AppNotification(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val body: String = "",
    val type: String = "reminder",
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

fun NotificationEntity.toNotification() = AppNotification(id, userId, title, body, type, isRead, createdAt)
fun AppNotification.toEntity() = NotificationEntity(id, userId, title, body, type, isRead, createdAt)
