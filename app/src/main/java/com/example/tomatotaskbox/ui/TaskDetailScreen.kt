package com.example.tomatotaskbox.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tomatotaskbox.models.Task
import com.example.tomatotaskbox.models.TaskStatus
import com.example.tomatotaskbox.models.TaskPriority
import com.example.tomatotaskbox.viewmodels.MainViewModel
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    viewModel: MainViewModel,
    taskId: Long?,
    navController: NavController
) {
    var isEditing by remember { mutableStateOf(false) }
    val tasks by viewModel.tasks.collectAsState()
    val task = tasks.find { it.id == taskId }

    // If task not found, show error and return
    if (task == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Task not found")
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditing) "Edit Task" else "Task Details",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (!isEditing) {
                        IconButton(onClick = { isEditing = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (isEditing) {
            EditTaskContent(
                task = task,
                onTaskUpdated = { updatedTask ->
                    viewModel.updateTask(updatedTask)
                    isEditing = false
                },
                onCancel = { isEditing = false },
                modifier = Modifier.padding(padding)
            )
        } else {
            TaskDetailContent(
                task = task,
                onTaskStatusChange = { viewModel.completeTask(task) },
                modifier = Modifier.padding(padding)
            )
        }
    }
}



@Composable
private fun TaskDetailContent(
    task: Task,
    onTaskStatusChange: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title and Status
        Card {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Switch(
                        checked = task.status == TaskStatus.COMPLETED,
                        onCheckedChange = { onTaskStatusChange() }
                    )
                }

                // Priority Badge
                Surface(
                    color = when (task.priority) {
                        TaskPriority.HIGH -> MaterialTheme.colorScheme.error
                        TaskPriority.MEDIUM -> MaterialTheme.colorScheme.secondary
                        TaskPriority.LOW -> MaterialTheme.colorScheme.tertiary
                    },
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(
                        text = task.priority.name,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }

        // Description
        Card {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Description",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = task.description ?: "No description provided",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        // Time Estimates and Due Date
        Card {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Time Details",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))

                task.estimatedTimeMinutes?.let {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Estimated Time")
                        Text("$it minutes")
                    }
                }

                task.dueDate?.let {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Due Date")
                        Text(it.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditTaskContent(
    task: Task,
    onTaskUpdated: (Task) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf(task.title) }
    var description by remember { mutableStateOf(task.description ?: "") }
    var priority by remember { mutableStateOf(task.priority) }
    var showPriorityDropdown by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        ExposedDropdownMenuBox(
            expanded = showPriorityDropdown,
            onExpandedChange = { showPriorityDropdown = !showPriorityDropdown }
        ) {
            TextField(
                value = priority.name,
                onValueChange = { },
                readOnly = true,
                label = { Text("Priority") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showPriorityDropdown) },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = showPriorityDropdown,
                onDismissRequest = { showPriorityDropdown = false }
            ) {
                TaskPriority.entries.forEach { priorityOption ->
                    DropdownMenuItem(
                        text = { Text(priorityOption.name) },
                        onClick = {
                            priority = priorityOption
                            showPriorityDropdown = false
                        }
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel")
            }
            Button(
                onClick = {
                    // Make sure to preserve all other task properties when updating
                    val updatedTask = task.copy(
                        title = title,
                        description = description.ifBlank { null },
                        priority = priority
                    )
                    onTaskUpdated(updatedTask)
                },
                modifier = Modifier.weight(1f),
                enabled = title.isNotBlank()
            ) {
                Text("Save")
            }
        }
    }
}