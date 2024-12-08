package com.example.tomatotaskbox.ui

import android.media.MediaPlayer
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tomatotaskbox.R
import com.example.tomatotaskbox.models.Task
import com.example.tomatotaskbox.viewmodels.MainViewModel
import kotlinx.coroutines.delay
import kotlin.math.floor

@Composable
fun PomodoroTimerScreen(
    viewModel: MainViewModel,
    navController: NavController
) {
    var isWorkSession by remember { mutableStateOf(true) }
    var selectedTask by remember { mutableStateOf<Task?>(null) }
    val tasks by viewModel.tasks.collectAsState()
    val context = LocalContext.current

    // Timer state
    var timeRemaining by remember { mutableStateOf(25 * 60) } // 25 minutes in seconds
    var isTimerRunning by remember { mutableStateOf(false) }

    // Create MediaPlayer
    val mediaPlayer = remember {
        MediaPlayer.create(context, R.raw.timer_end)
    }
    // Cleanup MediaPlayer when leaving the screen
    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
        }
    }

    // Timer effect
    LaunchedEffect(isTimerRunning, isWorkSession) {
        while (isTimerRunning && timeRemaining > 0) {
            delay(1000L) // Wait for 1 second
            timeRemaining--

            // When timer reaches 0
            if (timeRemaining == 0) {
                isTimerRunning = false
                // Play sound
                playTimerFinishSound(mediaPlayer)
                // Record the completed session
                if (isWorkSession) {
                    viewModel.recordWorkSession(
                        taskId = selectedTask?.id,
                        duration = 25,
                        isBreakSession = false
                    )
                }
            }
        }
    }

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
            fontSize = 72.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Session Type Text
        Text(
            text = if (isWorkSession) "Work Session" else "Break Time",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Task Selection (only show during work sessions)
        if (isWorkSession) {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Current Task",
                        style = MaterialTheme.typography.titleMedium
                    )
                    OutlinedButton(
                        onClick = { /* Implement task selection dialog */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Text(selectedTask?.title ?: "Select a task (optional)")
                    }
                }
            }
        }

        // Timer Controls
        Row(
            modifier = Modifier.padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Start/Pause Button
            Button(
                onClick = {
                    if (!isTimerRunning && timeRemaining == 0) {
                        // Reset timer if it finished
                        timeRemaining = if (isWorkSession) 25 * 60 else 5 * 60
                    }
                    isTimerRunning = !isTimerRunning
                },
                modifier = Modifier.width(120.dp)
            ) {
                Text(if (!isTimerRunning) "Start" else "Pause")
            }

            // Reset Button
            OutlinedButton(
                onClick = {
                    isTimerRunning = false
                    timeRemaining = if (isWorkSession) 25 * 60 else 5 * 60
                },
                modifier = Modifier.width(120.dp)
            ) {
                Text("Reset")
            }
        }

        // Switch Session Type Button
        OutlinedButton(
            onClick = {
                isTimerRunning = false
                isWorkSession = !isWorkSession
                timeRemaining = if (isWorkSession) 25 * 60 else  5 * 60
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text(if (isWorkSession) "Switch to Break" else "Switch to Work")
        }
    }
}

private fun playTimerFinishSound(mediaPlayer: MediaPlayer) {
    try {
        mediaPlayer.seekTo(0) // Reset to start
        mediaPlayer.start()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}


private fun formatTime(seconds: Int): String {
    val minutes = floor(seconds / 60.0).toInt()
    val remainingSeconds = seconds % 60
    return "%02d:%02d".format(minutes, remainingSeconds)
}