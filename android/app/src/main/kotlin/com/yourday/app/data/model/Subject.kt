package com.yourday.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subjects")
data class SubjectEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val name: String,
    val color: String = "#7C6FFF",
    val order: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class Subject(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val color: String = "#7C6FFF",
    val order: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

fun SubjectEntity.toSubject() = Subject(id, userId, name, color, order, createdAt)
fun Subject.toEntity() = SubjectEntity(id, userId, name, color, order, createdAt)
