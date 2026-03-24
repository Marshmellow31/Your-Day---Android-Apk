package com.yourday.app.data.repository

import com.yourday.app.data.local.dao.GoalDao
import com.yourday.app.data.model.PersonalGoal
import com.yourday.app.data.model.toEntity
import com.yourday.app.data.model.toGoal
import com.yourday.app.data.remote.GoalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GoalRepositoryImpl @Inject constructor(
    private val goalDao: GoalDao,
    private val goalDataSource: GoalDataSource
) : GoalRepository {

    override fun getGoalsFlow(userId: String): Flow<List<PersonalGoal>> =
        goalDao.getGoalsFlow(userId).map { it.map { e -> e.toGoal() } }

    override suspend fun createGoal(goal: PersonalGoal): Result<String> {
        val result = goalDataSource.createGoal(goal)
        result.onSuccess { id -> goalDao.insertGoal(goal.copy(id = id).toEntity()) }
        return result
    }

    override suspend fun updateGoal(id: String, data: Map<String, Any?>): Result<Unit> =
        goalDataSource.updateGoal(id, data)

    override suspend fun deleteGoal(id: String, userId: String): Result<Unit> {
        val result = goalDataSource.deleteGoal(id)
        result.onSuccess {
            val entity = goalDao.getById(id)
            entity?.let { goalDao.deleteGoal(it) }
        }
        return result
    }

    override suspend fun syncFromFirestore(userId: String): Result<Unit> = runCatching {
        val goals = goalDataSource.getGoals(userId).getOrThrow()
        goalDao.insertGoals(goals.map { it.toEntity() })
    }
}
