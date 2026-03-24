package com.yourday.app.data.repository

import com.yourday.app.data.local.dao.TaskDao
import com.yourday.app.data.model.Task
import com.yourday.app.data.model.toEntity
import com.yourday.app.data.model.toTask
import com.yourday.app.data.remote.TaskDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao,
    private val taskDataSource: TaskDataSource
) : TaskRepository {

    override fun getTasksFlow(userId: String): Flow<List<Task>> =
        taskDao.getTasksFlow(userId).map { it.map { e -> e.toTask() } }

    override fun getPendingTasksFlow(userId: String): Flow<List<Task>> =
        taskDao.getPendingTasksFlow(userId).map { it.map { e -> e.toTask() } }

    override suspend fun createTask(task: Task): Result<String> {
        val result = taskDataSource.createTask(task)
        result.onSuccess { id ->
            taskDao.insertTask(task.copy(id = id).toEntity())
        }
        return result
    }

    override suspend fun updateTask(taskId: String, data: Map<String, Any?>): Result<Unit> {
        val result = taskDataSource.updateTask(taskId, data)
        result.onSuccess {
            val existing = taskDao.getTaskById(taskId)
            if (existing != null) {
                val updated = existing.copy(
                    isCompleted = data["isCompleted"] as? Boolean ?: existing.isCompleted,
                    completedAt = data["completedAt"] as? Long ?: existing.completedAt,
                    updatedAt = System.currentTimeMillis()
                )
                taskDao.updateTask(updated)
            }
        }
        return result
    }

    override suspend fun completeTask(taskId: String): Result<Unit> {
        val now = System.currentTimeMillis()
        return updateTask(taskId, mapOf(
            "isCompleted" to true,
            "completedAt" to com.google.firebase.Timestamp(java.util.Date(now))
        ))
    }

    override suspend fun deleteTask(taskId: String, userId: String): Result<Unit> {
        val result = taskDataSource.deleteTask(taskId)
        result.onSuccess {
            val entity = taskDao.getTaskById(taskId)
            entity?.let { taskDao.deleteTask(it) }
        }
        return result
    }

    override suspend fun syncTasksFromFirestore(userId: String): Result<Unit> = runCatching {
        // Sync via the real-time flow; for manual sync collect once
        taskDataSource.getTasksFlow(userId)
        Unit
    }
}
