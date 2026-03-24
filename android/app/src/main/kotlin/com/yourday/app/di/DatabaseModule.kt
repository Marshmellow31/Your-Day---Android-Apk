package com.yourday.app.di

import android.content.Context
import androidx.room.Room
import com.yourday.app.data.local.YourDayDatabase
import com.yourday.app.data.local.dao.GoalDao
import com.yourday.app.data.local.dao.NotificationDao
import com.yourday.app.data.local.dao.SubjectDao
import com.yourday.app.data.local.dao.TaskDao
import com.yourday.app.data.local.dao.TopicDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideYourDayDatabase(@ApplicationContext context: Context): YourDayDatabase =
        Room.databaseBuilder(
            context,
            YourDayDatabase::class.java,
            "yourday.db"
        )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideTaskDao(db: YourDayDatabase): TaskDao = db.taskDao()

    @Provides
    fun provideSubjectDao(db: YourDayDatabase): SubjectDao = db.subjectDao()

    @Provides
    fun provideTopicDao(db: YourDayDatabase): TopicDao = db.topicDao()

    @Provides
    fun provideGoalDao(db: YourDayDatabase): GoalDao = db.goalDao()

    @Provides
    fun provideNotificationDao(db: YourDayDatabase): NotificationDao = db.notificationDao()
}
