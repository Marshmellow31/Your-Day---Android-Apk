package com.yourday.app.di

import com.yourday.app.data.remote.AuthDataSource
import com.yourday.app.data.remote.GoalDataSource
import com.yourday.app.data.remote.NotificationDataSource
import com.yourday.app.data.remote.SubjectDataSource
import com.yourday.app.data.remote.TaskDataSource
import com.yourday.app.data.remote.UserDataSource
import com.yourday.app.data.repository.AuthRepository
import com.yourday.app.data.repository.AuthRepositoryImpl
import com.yourday.app.data.repository.GoalRepository
import com.yourday.app.data.repository.GoalRepositoryImpl
import com.yourday.app.data.repository.NotificationRepository
import com.yourday.app.data.repository.NotificationRepositoryImpl
import com.yourday.app.data.repository.SubjectRepository
import com.yourday.app.data.repository.SubjectRepositoryImpl
import com.yourday.app.data.repository.TaskRepository
import com.yourday.app.data.repository.TaskRepositoryImpl
import com.yourday.app.data.repository.UserRepository
import com.yourday.app.data.repository.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds @Singleton
    abstract fun bindTaskRepository(impl: TaskRepositoryImpl): TaskRepository

    @Binds @Singleton
    abstract fun bindSubjectRepository(impl: SubjectRepositoryImpl): SubjectRepository

    @Binds @Singleton
    abstract fun bindGoalRepository(impl: GoalRepositoryImpl): GoalRepository

    @Binds @Singleton
    abstract fun bindNotificationRepository(impl: NotificationRepositoryImpl): NotificationRepository
}
