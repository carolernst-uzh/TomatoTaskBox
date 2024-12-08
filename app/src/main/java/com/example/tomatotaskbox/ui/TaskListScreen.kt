package com.example.tomatotaskbox.ui


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tomatotaskbox.models.Task
import com.example.tomatotaskbox.models.TaskPriority
import com.example.tomatotaskbox.models.TaskStatus
import com.example.tomatotaskbox.viewmodels.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    viewModel: MainViewModel,
    navController: NavController
) {
    var showAddTaskDialog by remember { mutableStateOf(false) }
    // Collect tasks from viewModel
    val tasks by viewModel.tasks.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddTaskDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Display tasks count for debugging
            Text(
                text = "Tasks: ${tasks.size}",
                modifier = Modifier.padding(16.dp)
            )

            // Daily Goal Progress
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Daily Productivity Goal",
                        style = MaterialTheme.typography.titleMedium
                    )
                    val dailyGoal by viewModel.dailyGoal.collectAsState()
                    val completedSessions by viewModel.completedSessions.collectAsState()

                    LinearProgressIndicator(
                        progress = if (dailyGoal > 0) completedSessions.toFloat() / dailyGoal else 0f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )
                    Text(
                        text = "$completedSessions / $dailyGoal sessions",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Task List
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(tasks) { task ->
                    TaskListItem(
                        task = task,
                        onTaskClick = {
                            navController.navigate("task-detail/${task.id}")
                        },
                        onCompleteTask = {
                            viewModel.completeTask(task)
                        }
                    )
                }
            }
        }

        if (showAddTaskDialog) {
            AddTaskDialog(
                onDismiss = { showAddTaskDialog = false },
                onTaskAdded = { newTask: Task ->
                    viewModel.addTask(newTask)
                    showAddTaskDialog = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListItem(
    task: Task,
    onTaskClick: () -> Unit,
    onCompleteTask: () -> Unit
) {
    Card(
        onClick = onTaskClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium
                )
                if (!task.description.isNullOrEmpty()) {
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2
                    )
                }

                // Priority badge
                Surface(
                    color = when (task.priority) {
                        TaskPriority.HIGH -> MaterialTheme.colorScheme.error
                        TaskPriority.MEDIUM -> MaterialTheme.colorScheme.secondary
                        TaskPriority.LOW -> MaterialTheme.colorScheme.tertiary
                    },
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = task.priority.name,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            if (task.status != TaskStatus.COMPLETED) {
                IconButton(onClick = onCompleteTask) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Complete Task",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Task Completed",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onTaskAdded: (Task) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(TaskPriority.MEDIUM) }
    var showPriorityDropdown by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Task") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank()) {
                        onTaskAdded(
                            Task(
                                title = title,
                                description = description.ifBlank { null },
                                categoryId = 1, // Default category
                                priority = priority,
                                status = TaskStatus.NOT_STARTED
                            )
                        )
                    }
                },
                enabled = title.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}