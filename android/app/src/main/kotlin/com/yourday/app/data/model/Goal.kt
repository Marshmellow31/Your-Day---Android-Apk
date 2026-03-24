package com.yourday.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val category: String = "custom",
    val totalTarget: Int = 1,
    val unit: String = "sessions",
    val durationDays: Int = 30,
    val startDate: String = "",
    val endDate: String? = null,
    val dailyTarget: Int = 1,
    val priority: String = "medium",
    val autoAddDaily: Boolean = true,
    val status: String = "active",
    val totalProgress: Int = 0,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class PersonalGoal(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val category: String = "custom",
    val totalTarget: Int = 1,
    val unit: String = "sessions",
    val durationDays: Int = 30,
    val startDate: String = "",
    val endDate: String? = null,
    val dailyTarget: Int = 1,
    val priority: String = "medium",
    val autoAddDaily: Boolean = true,
    val status: String = "active",
    val totalProgress: Int = 0,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    val progressPercent: Float
        get() = if (totalTarget > 0) (totalProgress.toFloat() / totalTarget).coerceIn(0f, 1f) else 0f
}

fun GoalEntity.toGoal() = PersonalGoal(id, userId, title, category, totalTarget, unit, durationDays, startDate, endDate, dailyTarget, priority, autoAddDaily, status, totalProgress, notes, createdAt)
fun PersonalGoal.toEntity() = GoalEntity(id, userId, title, category, totalTarget, unit, durationDays, startDate, endDate, dailyTarget, priority, autoAddDaily, status, totalProgress, notes, createdAt)
