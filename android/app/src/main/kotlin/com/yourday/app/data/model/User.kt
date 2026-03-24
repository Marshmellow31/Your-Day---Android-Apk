package com.yourday.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val uid: String,
    val displayName: String = "",
    val email: String = "",
    val photoUrl: String? = null,
    val theme: String = "dark",
    val weekStartDay: String = "monday",
    val notificationEnabled: Boolean = false,
    val defaultMinutesBefore: Int = 30,
    val studyGoals: String = "",
    val subjectsGrouped: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

// Network / domain model (not stored in Room directly, used for Firestore mapping)
data class User(
    val uid: String = "",
    val displayName: String = "",
    val email: String = "",
    val photoUrl: String? = null,
    val theme: String = "dark",
    val weekStartDay: String = "monday",
    val notificationEnabled: Boolean = false,
    val defaultMinutesBefore: Int = 30,
    val studyGoals: String = "",
    val subjectsGrouped: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

fun UserEntity.toUser() = User(uid, displayName, email, photoUrl, theme, weekStartDay, notificationEnabled, defaultMinutesBefore, studyGoals, subjectsGrouped, createdAt)
fun User.toEntity() = UserEntity(uid, displayName, email, photoUrl, theme, weekStartDay, notificationEnabled, defaultMinutesBefore, studyGoals, subjectsGrouped, createdAt)
