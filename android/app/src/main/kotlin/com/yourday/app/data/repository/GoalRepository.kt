package com.yourday.app.data.repository

import com.yourday.app.data.model.PersonalGoal
import kotlinx.coroutines.flow.Flow

interface GoalRepository {
    fun getGoalsFlow(userId: String): Flow<List<PersonalGoal>>
    suspend fun createGoal(goal: PersonalGoal): Result<String>
    suspend fun updateGoal(id: String, data: Map<String, Any?>): Result<Unit>
    suspend fun deleteGoal(id: String, userId: String): Result<Unit>
    suspend fun syncFromFirestore(userId: String): Result<Unit>
}
