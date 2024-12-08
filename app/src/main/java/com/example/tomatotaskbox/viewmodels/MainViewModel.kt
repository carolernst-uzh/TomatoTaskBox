package com.example.tomatotaskbox.viewmodels

import com.example.tomatotaskbox.data.TomatoTaskDatabase
import com.example.tomatotaskbox.models.Task
import com.example.tomatotaskbox.models.TaskStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDateTime
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tomatotaskbox.models.WorkSession
import kotlinx.coroutines.launch


class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = TomatoTaskDatabase.getDatabase(application)
    private val taskDao = database.taskDao()
    private val workSessionDao = database.workSessionDao()

    // Use MutableStateFlow for tasks instead
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    private val _dailyGoal = MutableStateFlow(4) // Default 4 Pomodoro sessions
    val dailyGoal: StateFlow<Int> = _dailyGoal.asStateFlow()

    private val _completedSessions = MutableStateFlow(0)
    val completedSessions: StateFlow<Int> = _completedSessions.asStateFlow()

    init {
        loadTasks()
        updateCompletedSessions()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            // Now directly assign the result to _tasks.value
            _tasks.value = taskDao.getAllTasks()
        }
    }

    fun addTask(task: Task) {
        viewModelScope.launch {
            taskDao.insert(task)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            taskDao.update(task)
        }
    }

    fun completeTask(task: Task) {
        viewModelScope.launch {
            val updatedTask = task.copy(
                status = TaskStatus.COMPLETED,
                completedAt = LocalDateTime.now()
            )
            taskDao.update(updatedTask)
        }
    }

    fun recordWorkSession(taskId: Long?, duration: Int, isBreakSession: Boolean) {
        viewModelScope.launch {
            val workSession = WorkSession(
                taskId = taskId,
                startTime = LocalDateTime.now(),
                endTime = LocalDateTime.now().plusMinutes(duration.toLong()),
                isBreakSession = isBreakSession,
                duration = duration
            )
            workSessionDao.insert(workSession)
            if (!isBreakSession) {
                _completedSessions.value++
            }
        }
    }

    private fun updateCompletedSessions() {
        viewModelScope.launch {
            val todaySessions = workSessionDao.getTodaysSessions(
                LocalDateTime.now().minusDays(1)
            )
            _completedSessions.value = todaySessions.size
        }
    }
}