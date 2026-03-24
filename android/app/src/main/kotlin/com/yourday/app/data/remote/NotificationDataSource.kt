package com.yourday.app.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.yourday.app.data.model.AppNotification
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private fun col() = firestore.collection("notifications")

    suspend fun getNotifications(userId: String): Result<List<AppNotification>> = runCatching {
        val snap = col().whereEqualTo("userId", userId).get().await()
        snap.documents.mapNotNull { doc ->
            val d = doc.data ?: return@mapNotNull null
            AppNotification(
                id = doc.id,
                userId = d["userId"] as? String ?: "",
                title = d["title"] as? String ?: "",
                body = d["body"] as? String ?: "",
                type = d["type"] as? String ?: "reminder",
                isRead = d["isRead"] as? Boolean ?: false,
                createdAt = (d["createdAt"] as? com.google.firebase.Timestamp)?.toDate()?.time ?: System.currentTimeMillis()
            )
        }.sortedByDescending { it.createdAt }
    }

    suspend fun markRead(notificationId: String): Result<Unit> = runCatching {
        col().document(notificationId).update("isRead", true).await()
    }

    suspend fun createNotification(notification: AppNotification): Result<String> = runCatching {
        val data = mapOf(
            "userId" to notification.userId,
            "title" to notification.title,
            "body" to notification.body,
            "type" to notification.type,
            "isRead" to false,
            "createdAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()
        )
        col().add(data).await().id
    }
}
