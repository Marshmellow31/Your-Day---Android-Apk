package com.yourday.app.data.local.dao

import androidx.room.*
import com.yourday.app.data.model.TopicEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TopicDao {
    @Query("SELECT * FROM topics WHERE userId = :userId AND subjectId = :subjectId ORDER BY `order` ASC, createdAt ASC")
    fun getTopicsFlow(userId: String, subjectId: String): Flow<List<TopicEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopic(topic: TopicEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopics(topics: List<TopicEntity>)

    @Update
    suspend fun updateTopic(topic: TopicEntity)

    @Delete
    suspend fun deleteTopic(topic: TopicEntity)

    @Query("DELETE FROM topics WHERE userId = :userId")
    suspend fun deleteAllForUser(userId: String)
}
