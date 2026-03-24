package com.yourday.app.data.repository

import com.yourday.app.data.model.User

interface UserRepository {
    suspend fun getUser(uid: String): Result<User?>
    suspend fun createUser(uid: String, user: User): Result<Unit>
    suspend fun updateUser(uid: String, data: Map<String, Any?>): Result<Unit>
    suspend fun saveFcmToken(uid: String, token: String): Result<Unit>
}
