package com.yourday.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "topics")
data class TopicEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val subjectId: String,
    val name: String,
    val order: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class Topic(
    val id: String = "",
    val userId: String = "",
    val subjectId: String = "",
    val name: String = "",
    val order: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

fun TopicEntity.toTopic() = Topic(id, userId, subjectId, name, order, createdAt)
fun Topic.toEntity() = TopicEntity(id, userId, subjectId, name, order, createdAt)
