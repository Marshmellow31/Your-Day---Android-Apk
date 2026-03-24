package com.yourday.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class Priority { LOW, MEDIUM, HIGH }

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val subjectId: String? = null,
    val topicId: String? = null,
    val title: String,
    val description: String = "",
    val priority: String = "medium",
    val dueDate: Long? = null,
    val reminderTime: Long? = null,
    val isCompleted: Boolean = false,
    val completedAt: Long? = null,
    val reminderSent: Boolean = false,
    val snoozedUntil: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class Task(
    val id: String = "",
    val userId: String = "",
    val subjectId: String? = null,
    val topicId: String? = null,
    val title: String = "",
    val description: String = "",
    val priority: String = "medium",
    val dueDate: Long? = null,
    val reminderTime: Long? = null,
    val isCompleted: Boolean = false,
    val completedAt: Long? = null,
    val reminderSent: Boolean = false,
    val snoozedUntil: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
) {
    val priorityEnum: Priority
        get() = when (priority.lowercase()) {
            "high" -> Priority.HIGH
            "low" -> Priority.LOW
            else -> Priority.MEDIUM
        }

    val isOverdue: Boolean
        get() = dueDate != null && dueDate < System.currentTimeMillis() && !isCompleted
}

fun TaskEntity.toTask() = Task(id, userId, subjectId, topicId, title, description, priority, dueDate, reminderTime, isCompleted, completedAt, reminderSent, snoozedUntil, createdAt)
fun Task.toEntity() = TaskEntity(id, userId, subjectId, topicId, title, description, priority, dueDate, reminderTime, isCompleted, completedAt, reminderSent, snoozedUntil, createdAt)
