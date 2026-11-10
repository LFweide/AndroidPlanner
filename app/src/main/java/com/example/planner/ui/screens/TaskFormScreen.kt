package com.example.planner.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.planner.R
import com.example.planner.ui.formatDate
import com.example.planner.ui.formatTime
import com.example.planner.ui.tasks.TasksViewModel
import com.example.planner.ui.toLocal
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

// Why: one form for both "new" and "edit". All input lives in rememberSaveable, so it survives rotation,
// and the column scrolls so every field stays reachable in landscape.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskFormScreen(
    taskId: Long,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TasksViewModel = viewModel()
) {
    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    // Default for a new task: the next full hour.
    var dueAt by rememberSaveable {
        mutableLongStateOf(ZonedDateTime.now().truncatedTo(ChronoUnit.HOURS).plusHours(1).toInstant().toEpochMilli())
    }
    var showTitleError by rememberSaveable { mutableStateOf(false) }
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var showTimePicker by rememberSaveable { mutableStateOf(false) }
    // Without this flag, the stored task would overwrite the user's input again after a rotation.
    var loaded by rememberSaveable { mutableStateOf(false) }
    var saving by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(taskId) {
        if (!loaded && taskId != TasksViewModel.NEW_TASK_ID) {
            viewModel.find(taskId)?.let {
                title = it.title
                description = it.description
                dueAt = it.dueAt
            }
        }
        loaded = true
    }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text(stringResource(R.string.task_title_label)) },
            isError = showTitleError && title.isBlank(),
            supportingText = {
                if (showTitleError && title.isBlank()) Text(stringResource(R.string.task_title_error))
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text(stringResource(R.string.task_description_label)) },
            minLines = 3,
            modifier = Modifier.fillMaxWidth()
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = { showDatePicker = true }) { Text(formatDate(dueAt)) }
            OutlinedButton(onClick = { showTimePicker = true }) { Text(formatTime(dueAt)) }
        }
        Button(
            onClick = {
                if (title.isBlank()) {
                    showTitleError = true
                } else if (!saving) {
                    // Guards against a double tap saving the task twice.
                    saving = true
                    viewModel.save(taskId, title.trim(), description.trim(), dueAt, onSaved = onDone)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text(stringResource(R.string.action_save)) }
    }

    if (showDatePicker) {
        // The date picker works in UTC midnight millis, so convert to and from the local date.
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = toLocal(dueAt).toLocalDate().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let { picked ->
                        val date = Instant.ofEpochMilli(picked).atZone(ZoneOffset.UTC).toLocalDate()
                        dueAt = withDate(dueAt, date)
                    }
                    showDatePicker = false
                }) { Text(stringResource(R.string.action_ok)) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text(stringResource(R.string.action_cancel)) }
            }
        ) { DatePicker(state = pickerState) }
    }

    if (showTimePicker) {
        val local = toLocal(dueAt)
        val pickerState = rememberTimePickerState(initialHour = local.hour, initialMinute = local.minute)
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    dueAt = local.withHour(pickerState.hour).withMinute(pickerState.minute)
                        .toInstant().toEpochMilli()
                    showTimePicker = false
                }) { Text(stringResource(R.string.action_ok)) }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text(stringResource(R.string.action_cancel)) }
            },
            text = { TimePicker(state = pickerState) }
        )
    }
}

// Keeps the time of day and replaces the date.
private fun withDate(millis: Long, date: LocalDate): Long =
    toLocal(millis).toLocalTime().atDate(date).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
