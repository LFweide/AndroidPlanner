package com.example.planner.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.planner.R
import com.example.planner.ui.formatDateTime
import com.example.planner.ui.tasks.TasksViewModel

// Why: the Taken tab. Shows all tasks, lets you tick them done, delete them (after confirming),
// open one to edit, or add a new one with the FAB.
@Composable
fun TasksScreen(
    onAddTask: () -> Unit,
    onEditTask: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TasksViewModel = viewModel()
) {
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    // Only the id is remembered, so the dialog survives rotation; the task itself is looked up again.
    var deleteId by rememberSaveable { mutableStateOf<Long?>(null) }
    val taskToDelete = tasks.firstOrNull { it.id == deleteId }

    Box(modifier = modifier.fillMaxSize()) {
        if (tasks.isEmpty()) {
            Text(
                text = stringResource(R.string.tasks_empty),
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            LazyColumn(
                // Extra bottom space so the last row is not hidden behind the FAB.
                contentPadding = PaddingValues(bottom = 88.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(tasks, key = { it.id }) { task ->
                    ListItem(
                        modifier = Modifier.clickable { onEditTask(task.id) },
                        leadingContent = {
                            Checkbox(
                                checked = task.done,
                                onCheckedChange = { viewModel.setDone(task, it) }
                            )
                        },
                        headlineContent = { Text(task.title, maxLines = 2) },
                        supportingContent = { Text(formatDateTime(task.dueAt)) },
                        trailingContent = {
                            IconButton(onClick = { deleteId = task.id }) {
                                Icon(
                                    Icons.Filled.Delete,
                                    contentDescription = stringResource(R.string.task_delete)
                                )
                            }
                        }
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = onAddTask,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.task_add))
        }
    }

    if (taskToDelete != null) {
        AlertDialog(
            onDismissRequest = { deleteId = null },
            title = { Text(stringResource(R.string.delete_confirm_title)) },
            text = { Text(taskToDelete.title) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.delete(taskToDelete)
                    deleteId = null
                }) { Text(stringResource(R.string.action_delete)) }
            },
            dismissButton = {
                TextButton(onClick = { deleteId = null }) { Text(stringResource(R.string.action_cancel)) }
            }
        )
    }
}
