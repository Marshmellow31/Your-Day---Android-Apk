package com.yourday.app.data.repository

import com.yourday.app.data.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getTasksFlow(userId: String): Flow<List<Task>>
    fun getPendingTasksFlow(userId: String): Flow<List<Task>>
    suspend fun createTask(task: Task): Result<String>
    suspend fun updateTask(taskId: String, data: Map<String, Any?>): Result<Unit>
    suspend fun completeTask(taskId: String): Result<Unit>
    suspend fun deleteTask(taskId: String, userId: String): Result<Unit>
    suspend fun syncTasksFromFirestore(userId: String): Result<Unit>
}
