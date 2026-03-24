package com.yourday.app.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.yourday.app.data.model.PersonalGoal
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoalDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private fun goalCol() = firestore.collection("personalGoals")

    suspend fun getGoals(userId: String): Result<List<PersonalGoal>> = runCatching {
        val snap = goalCol().whereEqualTo("userId", userId).get().await()
        snap.documents.mapNotNull { doc ->
            val d = doc.data ?: return@mapNotNull null
            PersonalGoal(
                id = doc.id,
                userId = d["userId"] as? String ?: "",
                title = d["title"] as? String ?: "",
                category = d["category"] as? String ?: "custom",
                totalTarget = (d["totalTarget"] as? Long)?.toInt() ?: 1,
                unit = d["unit"] as? String ?: "sessions",
                durationDays = (d["durationDays"] as? Long)?.toInt() ?: 30,
                startDate = d["startDate"] as? String ?: "",
                endDate = d["endDate"] as? String,
                dailyTarget = (d["dailyTarget"] as? Long)?.toInt() ?: 1,
                priority = d["priority"] as? String ?: "medium",
                autoAddDaily = d["autoAddDaily"] as? Boolean ?: true,
                status = d["status"] as? String ?: "active",
                totalProgress = (d["totalProgress"] as? Long)?.toInt() ?: 0,
                notes = d["notes"] as? String ?: ""
            )
        }.sortedByDescending { it.createdAt }
    }

    suspend fun createGoal(goal: PersonalGoal): Result<String> = runCatching {
        val data = mapOf(
            "userId" to goal.userId,
            "title" to goal.title,
            "category" to goal.category,
            "totalTarget" to goal.totalTarget,
            "unit" to goal.unit,
            "durationDays" to goal.durationDays,
            "startDate" to goal.startDate,
            "endDate" to goal.endDate,
            "dailyTarget" to goal.dailyTarget,
            "priority" to goal.priority,
            "autoAddDaily" to goal.autoAddDaily,
            "status" to "active",
            "totalProgress" to 0,
            "notes" to goal.notes,
            "createdAt" to com.google.firebase.firestore.FieldValue.serverTimestamp(),
            "updatedAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()
        )
        goalCol().add(data).await().id
    }

    suspend fun updateGoal(id: String, data: Map<String, Any?>): Result<Unit> = runCatching {
        goalCol().document(id).update(data + mapOf("updatedAt" to com.google.firebase.firestore.FieldValue.serverTimestamp())).await()
    }

    suspend fun deleteGoal(id: String): Result<Unit> = runCatching {
        goalCol().document(id).delete().await()
    }
}
