package com.yourday.app.data.repository

import com.yourday.app.data.local.dao.SubjectDao
import com.yourday.app.data.model.Subject
import com.yourday.app.data.model.toEntity
import com.yourday.app.data.model.toSubject
import com.yourday.app.data.remote.SubjectDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SubjectRepositoryImpl @Inject constructor(
    private val subjectDao: SubjectDao,
    private val subjectDataSource: SubjectDataSource
) : SubjectRepository {

    override fun getSubjectsFlow(userId: String): Flow<List<Subject>> =
        subjectDao.getSubjectsFlow(userId).map { it.map { e -> e.toSubject() } }

    override suspend fun createSubject(subject: Subject): Result<String> {
        val result = subjectDataSource.createSubject(subject)
        result.onSuccess { id ->
            subjectDao.insertSubject(subject.copy(id = id).toEntity())
        }
        return result
    }

    override suspend fun updateSubject(id: String, data: Map<String, Any?>): Result<Unit> {
        return subjectDataSource.updateSubject(id, data)
    }

    override suspend fun deleteSubject(id: String, userId: String): Result<Unit> {
        val result = subjectDataSource.deleteSubject(id)
        result.onSuccess {
            val entity = subjectDao.getById(id)
            entity?.let { subjectDao.deleteSubject(it) }
        }
        return result
    }

    override suspend fun syncFromFirestore(userId: String): Result<Unit> = runCatching {
        val subjects = subjectDataSource.getSubjects(userId).getOrThrow()
        subjectDao.insertSubjects(subjects.map { it.toEntity() })
    }
}
