package com.example.tomatotaskbox.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PomodoroTimerService : Service() {
    private val binder = LocalBinder()
    private val _timeRemaining = MutableStateFlow(0L)
    val timeRemaining = _timeRemaining.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning = _isRunning.asStateFlow()

    private var timerJob: Job? = null
    private var totalSessionTime: Long = 0
    private var isWorkSession: Boolean = true

    inner class LocalBinder : Binder() {
        fun getService(): PomodoroTimerService = this@PomodoroTimerService
    }

    fun startTimer(isWork: Boolean = true, durationMinutes: Int = 25) {
        // Stop existing timer if running
        timerJob?.cancel()

        isWorkSession = isWork
        totalSessionTime = durationMinutes * 60L
        _timeRemaining.value = totalSessionTime
        _isRunning.value = true

        timerJob = CoroutineScope(Dispatchers.Default).launch {
            while (_timeRemaining.value > 0) {
                delay(1000)
                _timeRemaining.value--
            }

            // Timer completed
            _isRunning.value = false
            showCompletionNotification()
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
        _isRunning.value = false
    }

    fun resumeTimer() {
        if (_timeRemaining.value > 0) {
            startTimer(isWorkSession, (_timeRemaining.value / 60).toInt())
        }
    }

    private fun showCompletionNotification() {
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(if (isWorkSession) "Work Session Complete" else "Break Complete")
            .setContentText("Time to ${if (isWorkSession) "take a break" else "get back to work"}!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        // TODO: Implement actual notification logic
    }

    override fun onBind(intent: Intent): IBinder = binder

    companion object {
        const val CHANNEL_ID = "PomodoroTimerChannel"
    }
}