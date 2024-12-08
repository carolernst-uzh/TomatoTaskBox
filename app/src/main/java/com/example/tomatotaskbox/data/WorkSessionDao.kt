package com.example.tomatotaskbox.data

import androidx.room.*
import com.example.tomatotaskbox.models.WorkSession
import java.time.LocalDateTime

@Dao
interface WorkSessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(workSession: WorkSession)

    @Update
    suspend fun update(workSession: WorkSession)

    @Delete
    suspend fun delete(workSession: WorkSession)

    @Query("SELECT * FROM work_sessions WHERE startTime >= :fromDate")
    suspend fun getTodaysSessions(fromDate: LocalDateTime): List<WorkSession>

    @Query("SELECT * FROM work_sessions WHERE taskId = :taskId")
    suspend fun getSessionsForTask(taskId: Long): List<WorkSession>

    @Query("SELECT COUNT(*) FROM work_sessions WHERE isBreakSession = 0 AND startTime >= :fromDate")
    suspend fun getCompletedWorkSessions(fromDate: LocalDateTime): Int

    @Query("SELECT * FROM work_sessions ORDER BY startTime DESC")
    suspend fun getAllSessions(): List<WorkSession>
}