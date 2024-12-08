package com.example.tomatotaskbox.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Embedded
import androidx.room.Relation
import java.time.LocalDateTime

// Enum for task priority
enum class TaskPriority {
    LOW, MEDIUM, HIGH
}

// Enum for task status
enum class TaskStatus {
    NOT_STARTED, IN_PROGRESS, COMPLETED
}

// Category Entity with support for hierarchical categories
@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val parentCategoryId: Long? = null
)

// Task Entity with comprehensive task information
@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String? = null,
    val categoryId: Long,
    val estimatedTimeMinutes: Int? = null,
    val dueDate: LocalDateTime? = null,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val status: TaskStatus = TaskStatus.NOT_STARTED,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val completedAt: LocalDateTime? = null
)

// Represents a Pomodoro work session
@Entity(tableName = "work_sessions")
data class WorkSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val taskId: Long? = null,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime? = null,
    val isBreakSession: Boolean = false,
    val duration: Int // in minutes
)

// Combined data class to show task with its category
data class TaskWithCategory(
    @Embedded val task: Task,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    val category: Category
)