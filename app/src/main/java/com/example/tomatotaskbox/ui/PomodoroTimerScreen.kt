package com.example.tomatotaskbox.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tomatotaskbox.viewmodels.MainViewModel
import com.example.tomatotaskbox.models.Task
import kotlin.math.floor

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun PomodoroTimerScreen(
    viewModel: MainViewModel,
    navController: NavController  // Added NavController parameter
) {
    var isWorkSession by remember { mutableStateOf(true) }
    var selectedTask by remember { mutableStateOf<Task?>(null) }
    val tasks by viewModel.tasks.collectAsState(initial = emptyList())

    // Timer state management
    var timeRemaining by remember { mutableStateOf(25 * 60L) }
    var isTimerRunning by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Timer Display
        Text(
            text = formatTime(timeRemaining),
            style = MaterialTheme.typography.displayLarge,
            fontSize = 48.sp
        )

        // Session Type Indicator
        Text(
            text = if (isWorkSession) "Work Session" else "Break Time",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // Task Selection Dropdown
        if (isWorkSession) {
            OutlinedButton(
                onClick = { /* Implement task selection */ },
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(selectedTask?.title ?: "Select a task (optional)")
            }
        }

        // Control Buttons
        Row(
            modifier = Modifier.padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Start/Pause Button
            Button(
                onClick = {
                    if (!isTimerRunning) {
                        // Start timer
                        isTimerRunning = true
                        // Simulate timer countdown (replace with actual timer logic)
                        timeRemaining = if (isWorkSession) 25 * 60L else 5 * 60L
                    } else {
                        // Pause timer
                        isTimerRunning = false
                    }
                }
            ) {
                Text(if (!isTimerRunning) "Start" else "Pause")
            }

            // Switch Session Type Button
            Button(
                onClick = {
                    isWorkSession = !isWorkSession
                    // Reset timer
                    timeRemaining = if (isWorkSession) 25 * 60L else 5 * 60L
                    isTimerRunning = false

                    // Record work session if completing a work session
                    if (!isWorkSession && selectedTask != null) {
                        viewModel.recordWorkSession(
                            taskId = selectedTask?.id,
                            duration = 25,
                            isBreakSession = false
                        )
                    }
                }
            ) {
                Text(if (isWorkSession) "Take Break" else "Back to Work")
            }
        }
    }
}

// Utility function to format time
private fun formatTime(seconds: Long): String {
    val minutes = floor(seconds / 60.0).toInt()
    val remainingSeconds = (seconds % 60).toInt()
    return "%02d:%02d".format(minutes, remainingSeconds)
}