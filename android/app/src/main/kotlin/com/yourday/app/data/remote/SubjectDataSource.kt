package com.yourday.app.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.yourday.app.data.model.Subject
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubjectDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private fun col() = firestore.collection("subjects")

    suspend fun getSubjects(userId: String): Result<List<Subject>> = runCatching {
        val snap = col().whereEqualTo("userId", userId).get().await()
        snap.documents.mapNotNull { doc ->
            val d = doc.data ?: return@mapNotNull null
            Subject(
                id = doc.id,
                userId = d["userId"] as? String ?: "",
                name = d["name"] as? String ?: "",
                color = d["color"] as? String ?: "#7C6FFF",
                order = (d["order"] as? Long)?.toInt() ?: 0,
                createdAt = (d["createdAt"] as? com.google.firebase.Timestamp)?.toDate()?.time ?: System.currentTimeMillis()
            )
        }.sortedWith(compareBy({ it.order }, { it.createdAt }))
    }

    suspend fun createSubject(subject: Subject): Result<String> = runCatching {
        val data = mapOf(
            "userId" to subject.userId,
            "name" to subject.name,
            "color" to subject.color,
            "order" to subject.order,
            "createdAt" to com.google.firebase.firestore.FieldValue.serverTimestamp(),
            "updatedAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()
        )
        col().add(data).await().id
    }

    suspend fun updateSubject(id: String, data: Map<String, Any?>): Result<Unit> = runCatching {
        col().document(id).set(data + mapOf("updatedAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()), SetOptions.merge()).await()
    }

    suspend fun deleteSubject(id: String): Result<Unit> = runCatching {
        col().document(id).delete().await()
    }
}
