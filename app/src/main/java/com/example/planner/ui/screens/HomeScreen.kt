package com.example.planner.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.planner.R
import com.example.planner.data.Task
import com.example.planner.ui.formatDateTime
import com.example.planner.ui.home.splitTasks
import com.example.planner.ui.tasks.TasksViewModel
import kotlinx.coroutines.delay

// Why: the Home tab. Shows overdue tasks, the next task as a card and the following upcoming ones.
// It reads the same task flow as Taken, so edits and ticks show up immediately.
@Composable
fun HomeScreen(
    onEditTask: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TasksViewModel = viewModel()
) {
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    // Why: "overdue" depends on the clock, so refresh it every 30 s while Home is visible.
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(30_000)
            now = System.currentTimeMillis()
        }
    }
    // Reading the clock again when tasks change keeps a task that was just added classified correctly.
    val sections = remember(tasks, now) { splitTasks(tasks, maxOf(now, System.currentTimeMillis())) }
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (sections.isEmpty) {
        Box(modifier = modifier.fillMaxSize()) {
            Text(
                text = stringResource(R.string.home_empty),
                modifier = Modifier.align(Alignment.Center)
            )
        }
        return
    }

    val next = sections.next
    val markDone: (Task) -> Unit = { viewModel.setDone(it, true) }

    if (isLandscape) {
        // Landscape: next card on the left, the lists on the right.
        Row(
            modifier = modifier.fillMaxSize().padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                if (next != null) NextCard(next, onEditTask, markDone)
            }
            TaskList(
                overdue = sections.overdue,
                upcoming = sections.upcoming,
                next = null,
                onEditTask = onEditTask,
                onDone = markDone,
                modifier = Modifier.weight(1f)
            )
        }
    } else {
        TaskList(
            overdue = sections.overdue,
            upcoming = sections.upcoming,
            next = next,
            onEditTask = onEditTask,
            onDone = markDone,
            modifier = modifier.fillMaxSize()
        )
    }
}

// Why: one scrolling list in order overdue, next card (portrait only: next is null in landscape), upcoming.
@Composable
private fun TaskList(
    overdue: List<Task>,
    upcoming: List<Task>,
    next: Task?,
    onEditTask: (Long) -> Unit,
    onDone: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (overdue.isNotEmpty()) {
            item(key = "overdue-header") {
                Text(
                    text = stringResource(R.string.home_overdue),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
            items(overdue, key = { "o${it.id}" }) { TaskRow(it, onEditTask, onDone, overdue = true) }
        }
        if (next != null) {
            item(key = "next") { NextCard(next, onEditTask, onDone) }
        }
        if (upcoming.isNotEmpty()) {
            item(key = "upcoming-header") {
                Text(
                    text = stringResource(R.string.home_upcoming),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            items(upcoming, key = { "u${it.id}" }) { TaskRow(it, onEditTask, onDone, overdue = false) }
        }
    }
}

@Composable
private fun NextCard(task: Task, onEditTask: (Long) -> Unit, onDone: (Task) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable { onEditTask(task.id) }) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(stringResource(R.string.home_next), style = MaterialTheme.typography.labelLarge)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(task.title, style = MaterialTheme.typography.headlineSmall, maxLines = 2)
                    Text(formatDateTime(task.dueAt), style = MaterialTheme.typography.bodyMedium)
                    task.contactName?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
                }
                Checkbox(checked = false, onCheckedChange = { onDone(task) })
            }
        }
    }
}

@Composable
private fun TaskRow(task: Task, onEditTask: (Long) -> Unit, onDone: (Task) -> Unit, overdue: Boolean) {
    ListItem(
        modifier = Modifier.clickable { onEditTask(task.id) },
        leadingContent = { Checkbox(checked = false, onCheckedChange = { onDone(task) }) },
        headlineContent = { Text(task.title, maxLines = 2) },
        supportingContent = {
            Text(
                formatDateTime(task.dueAt),
                color = if (overdue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    )
}
