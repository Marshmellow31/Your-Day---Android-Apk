package com.yourday.app.data.repository

import com.yourday.app.data.model.User
import com.yourday.app.data.remote.UserDataSource
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDataSource: UserDataSource
) : UserRepository {
    override suspend fun getUser(uid: String): Result<User?> = userDataSource.getUser(uid)
    override suspend fun createUser(uid: String, user: User): Result<Unit> = userDataSource.createUser(uid, user)
    override suspend fun updateUser(uid: String, data: Map<String, Any?>): Result<Unit> = userDataSource.updateUser(uid, data)
    override suspend fun saveFcmToken(uid: String, token: String): Result<Unit> = userDataSource.saveFcmToken(uid, token)
}
