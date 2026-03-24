package com.yourday.app.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.yourday.app.data.repository.GoalRepository
import com.yourday.app.data.repository.NotificationRepository
import com.yourday.app.data.repository.SubjectRepository
import com.yourday.app.data.repository.TaskRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import com.google.firebase.auth.FirebaseAuth

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val taskRepository: TaskRepository,
    private val subjectRepository: SubjectRepository,
    private val goalRepository: GoalRepository,
    private val notificationRepository: NotificationRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return Result.success()
        return try {
            subjectRepository.syncFromFirestore(userId)
            goalRepository.syncFromFirestore(userId)
            notificationRepository.syncFromFirestore(userId)
            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }
}
