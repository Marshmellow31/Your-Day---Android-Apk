package com.yourday.app.data.repository

import com.yourday.app.data.model.User
import com.yourday.app.data.remote.AuthDataSource
import com.yourday.app.data.remote.UserDataSource
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val userDataSource: UserDataSource
) : AuthRepository {

    override suspend fun signInWithEmail(email: String, password: String): Result<User> =
        authDataSource.signInWithEmail(email, password)

    override suspend fun registerWithEmail(email: String, password: String, displayName: String): Result<User> {
        val result = authDataSource.registerWithEmail(email, password, displayName)
        return result.mapCatching { firebaseUser ->
            val user = User(
                uid = firebaseUser.uid,
                displayName = displayName,
                email = firebaseUser.email ?: email
            )
            userDataSource.createUser(firebaseUser.uid, user).getOrThrow()
            user
        }
    }

    override suspend fun signInWithGoogle(idToken: String): Result<User> {
        val result = authDataSource.signInWithGoogle(idToken)
        return result.mapCatching { user ->
            val existing = userDataSource.getUser(user.uid).getOrNull()
            if (existing == null) userDataSource.createUser(user.uid, user).getOrThrow()
            user
        }
    }

    override suspend fun sendPasswordReset(email: String): Result<Unit> =
        authDataSource.sendPasswordReset(email)

    override suspend fun signOut() = authDataSource.signOut()

    override fun getCurrentUserId(): String? = authDataSource.currentUser?.uid

    override fun isLoggedIn(): Boolean = authDataSource.currentUser != null
}
