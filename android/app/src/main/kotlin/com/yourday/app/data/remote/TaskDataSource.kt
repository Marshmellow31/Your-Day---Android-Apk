package com.yourday.app.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.yourday.app.data.model.Task
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private fun col() = firestore.collection("tasks")

    fun getTasksFlow(userId: String): Flow<List<Task>> = callbackFlow {
        val listener = col()
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val tasks = snapshot?.documents?.mapNotNull { doc ->
                    documentToTask(doc.id, doc.data ?: return@mapNotNull null)
                } ?: emptyList()
                trySend(tasks.sortedByDescending { it.createdAt })
            }
        awaitClose { listener.remove() }
    }

    suspend fun createTask(task: Task): Result<String> = runCatching {
        val data = taskToMap(task)
        val ref = col().add(data).await()
        ref.id
    }

    suspend fun updateTask(taskId: String, data: Map<String, Any?>): Result<Unit> = runCatching {
        col().document(taskId).set(data, SetOptions.merge()).await()
    }

    suspend fun deleteTask(taskId: String): Result<Unit> = runCatching {
        col().document(taskId).delete().await()
    }

    private fun documentToTask(id: String, data: Map<String, Any>): Task {
        fun Long(value: Any?): Long? = when (value) {
            is com.google.firebase.Timestamp -> value.toDate().time
            is Long -> value
            is Double -> value.toLong()
            else -> null
        }
        return Task(
            id = id,
            userId = data["userId"] as? String ?: "",
            subjectId = data["subjectId"] as? String,
            topicId = data["topicId"] as? String,
            title = data["title"] as? String ?: "",
            description = data["description"] as? String ?: "",
            priority = data["priority"] as? String ?: "medium",
            dueDate = Long(data["dueDate"]),
            reminderTime = Long(data["reminderTime"]),
            isCompleted = data["isCompleted"] as? Boolean ?: false,
            completedAt = Long(data["completedAt"]),
            reminderSent = data["reminderSent"] as? Boolean ?: false,
            snoozedUntil = Long(data["snoozedUntil"]),
            createdAt = Long(data["createdAt"]) ?: System.currentTimeMillis()
        )
    }

    private fun taskToMap(task: Task): Map<String, Any?> = mapOf(
        "userId" to task.userId,
        "subjectId" to task.subjectId,
        "topicId" to task.topicId,
        "title" to task.title,
        "description" to task.description,
        "priority" to task.priority,
        "dueDate" to task.dueDate?.let { com.google.firebase.Timestamp(java.util.Date(it)) },
        "reminderTime" to task.reminderTime?.let { com.google.firebase.Timestamp(java.util.Date(it)) },
        "isCompleted" to task.isCompleted,
        "completedAt" to task.completedAt?.let { com.google.firebase.Timestamp(java.util.Date(it)) },
        "reminderSent" to task.reminderSent,
        "snoozedUntil" to task.snoozedUntil?.let { com.google.firebase.Timestamp(java.util.Date(it)) },
        "createdAt" to com.google.firebase.firestore.FieldValue.serverTimestamp(),
        "updatedAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()
    )
}
