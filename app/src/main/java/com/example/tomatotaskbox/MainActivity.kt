package com.example.tomatotaskbox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tomatotaskbox.ui.PomodoroTimerScreen
import com.example.tomatotaskbox.ui.TaskDetailScreen
import com.example.tomatotaskbox.ui.TaskListScreen
import com.example.tomatotaskbox.ui.theme.TomatoTaskBoxTheme
import com.example.tomatotaskbox.viewmodels.MainViewModel


class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TomatoTaskBoxTheme {
                val navController = rememberNavController()
                // Fixed state declaration
                val selectedTab = remember { mutableStateOf("tasks") }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        bottomBar = {
                            NavigationBar {
                                NavigationBarItem(
                                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Tasks") },
                                    label = { Text("Tasks") },
                                    selected = selectedTab.value == "tasks",
                                    onClick = {
                                        selectedTab.value = "tasks"
                                        navController.navigate("tasks") {
                                            popUpTo("tasks") { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )
                                NavigationBarItem(
                                    icon = { Icon(Icons.Default.PlayArrow, contentDescription = "Pomodoro") },
                                    label = { Text("Pomodoro") },
                                    selected = selectedTab.value == "pomodoro",
                                    onClick = {
                                        selectedTab.value = "pomodoro"
                                        navController.navigate("pomodoro") {
                                            popUpTo("tasks") { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )
                            }
                        }
                    ) { padding ->
                        NavHost(
                            navController = navController,
                            startDestination = "tasks",
                            modifier = Modifier.padding(padding)
                        ) {
                            composable("tasks") {
                                TaskListScreen(viewModel, navController)
                            }
                            composable("pomodoro") {
                                PomodoroTimerScreen(viewModel, navController)
                            }
                            composable(
                                route = "task-detail/{taskId}",
                                arguments = listOf(
                                    navArgument("taskId") {
                                        type = NavType.LongType
                                    }
                                )
                            ) { backStackEntry ->
                                val taskId = backStackEntry.arguments?.getLong("taskId")
                                TaskDetailScreen(
                                    viewModel = viewModel,
                                    taskId = taskId,
                                    navController = navController
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}