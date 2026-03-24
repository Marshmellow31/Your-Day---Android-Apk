package com.yourday.app.data.repository

import com.yourday.app.data.model.Subject
import kotlinx.coroutines.flow.Flow

interface SubjectRepository {
    fun getSubjectsFlow(userId: String): Flow<List<Subject>>
    suspend fun createSubject(subject: Subject): Result<String>
    suspend fun updateSubject(id: String, data: Map<String, Any?>): Result<Unit>
    suspend fun deleteSubject(id: String, userId: String): Result<Unit>
    suspend fun syncFromFirestore(userId: String): Result<Unit>
}
