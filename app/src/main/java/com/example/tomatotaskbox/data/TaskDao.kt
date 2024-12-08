package com.example.tomatotaskbox.data

import androidx.room.*
import com.example.tomatotaskbox.models.Task

@Dao
interface TaskDao {
    // Changed to suspend function instead of Flow
    @Query("SELECT * FROM tasks")
    suspend fun getAllTasks(): List<Task>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: Long): Task?
}