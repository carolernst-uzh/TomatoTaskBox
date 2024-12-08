package com.example.tomatotaskbox.data

import android.content.Context
import androidx.room.*
import com.example.tomatotaskbox.models.Category
import com.example.tomatotaskbox.models.Task
import com.example.tomatotaskbox.models.TaskPriority
import com.example.tomatotaskbox.models.TaskStatus
import com.example.tomatotaskbox.models.WorkSession
import java.time.LocalDateTime
import java.time.ZoneOffset

@Database(
    entities = [Task::class, Category::class, WorkSession::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TomatoTaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun categoryDao(): CategoryDao
    abstract fun workSessionDao(): WorkSessionDao

    companion object {
        @Volatile
        private var Instance: TomatoTaskDatabase? = null

        fun getDatabase(context: Context): TomatoTaskDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    TomatoTaskDatabase::class.java,
                    "tomato_task_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDateTime? {
        return value?.let { LocalDateTime.ofEpochSecond(it, 0, ZoneOffset.UTC) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): Long? {
        return date?.toEpochSecond(ZoneOffset.UTC)
    }

    @TypeConverter
    fun fromPriority(priority: TaskPriority): String {
        return priority.name
    }

    @TypeConverter
    fun toPriority(value: String): TaskPriority {
        return TaskPriority.valueOf(value)
    }

    @TypeConverter
    fun fromStatus(status: TaskStatus): String {
        return status.name
    }

    @TypeConverter
    fun toStatus(value: String): TaskStatus {
        return TaskStatus.valueOf(value)
    }
}