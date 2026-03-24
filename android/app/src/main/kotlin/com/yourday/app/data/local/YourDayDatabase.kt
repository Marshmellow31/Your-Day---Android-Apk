package com.yourday.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.yourday.app.data.local.dao.GoalDao
import com.yourday.app.data.local.dao.NotificationDao
import com.yourday.app.data.local.dao.SubjectDao
import com.yourday.app.data.local.dao.TaskDao
import com.yourday.app.data.local.dao.TopicDao
import com.yourday.app.data.model.GoalEntity
import com.yourday.app.data.model.NotificationEntity
import com.yourday.app.data.model.SubjectEntity
import com.yourday.app.data.model.TaskEntity
import com.yourday.app.data.model.TopicEntity

@Database(
    entities = [
        TaskEntity::class,
        SubjectEntity::class,
        TopicEntity::class,
        GoalEntity::class,
        NotificationEntity::class,
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class YourDayDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun subjectDao(): SubjectDao
    abstract fun topicDao(): TopicDao
    abstract fun goalDao(): GoalDao
    abstract fun notificationDao(): NotificationDao
}
