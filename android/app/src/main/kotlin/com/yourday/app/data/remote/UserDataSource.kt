package com.yourday.app.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.yourday.app.data.model.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private fun usersCol() = firestore.collection("users")

    suspend fun getUser(uid: String): Result<User?> = runCatching {
        val snap = usersCol().document(uid).get().await()
        if (!snap.exists()) return@runCatching null
        val d = snap.data ?: return@runCatching null
        val reminderSettings = d["reminderSettings"] as? Map<*, *>
        User(
            uid = uid,
            displayName = d["displayName"] as? String ?: "",
            email = d["email"] as? String ?: "",
            photoUrl = d["photoURL"] as? String,
            theme = d["theme"] as? String ?: "dark",
            weekStartDay = d["weekStartDay"] as? String ?: "monday",
            notificationEnabled = d["notificationEnabled"] as? Boolean ?: false,
            defaultMinutesBefore = (reminderSettings?.get("defaultMinutesBefore") as? Long)?.toInt() ?: 30,
            studyGoals = d["studyGoals"] as? String ?: "",
            subjectsGrouped = d["subjectsGrouped"] as? Boolean ?: true
        )
    }

    suspend fun createUser(uid: String, user: User): Result<Unit> = runCatching {
        val data = mapOf(
            "uid" to uid,
            "displayName" to user.displayName,
            "email" to user.email,
            "photoURL" to user.photoUrl,
            "theme" to "dark",
            "weekStartDay" to "monday",
            "notificationEnabled" to false,
            "reminderSettings" to mapOf("defaultMinutesBefore" to 30),
            "studyGoals" to "",
            "subjectsGrouped" to true,
            "createdAt" to com.google.firebase.firestore.FieldValue.serverTimestamp(),
            "updatedAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()
        )
        usersCol().document(uid).set(data, SetOptions.merge()).await()
    }

    suspend fun updateUser(uid: String, data: Map<String, Any?>): Result<Unit> = runCatching {
        usersCol().document(uid).set(data + mapOf("updatedAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()), SetOptions.merge()).await()
    }

    suspend fun saveFcmToken(uid: String, token: String): Result<Unit> = runCatching {
        usersCol().document(uid).collection("fcmTokens").document(token).set(
            mapOf(
                "token" to token,
                "platform" to "android",
                "createdAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()
            )
        ).await()
    }
}
