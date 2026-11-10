package com.example.planner.ui.tasks

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.planner.data.AppDatabase
import com.example.planner.data.Task
import com.example.planner.data.TaskRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Why: holds the task list for the screens and runs every write in viewModelScope.
// Room switches to its own background thread for suspend calls, so the main thread never waits on the DB.
class TasksViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = TaskRepository(AppDatabase.get(application).taskDao())

    val tasks: StateFlow<List<Task>> = repository.tasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    suspend fun find(id: Long): Task? = repository.find(id)

    // taskId == NEW_TASK_ID creates a task; otherwise the existing one is updated.
    // Updating copies the stored task, so fields this form does not edit (done, contact, reminder) survive.
    fun save(taskId: Long, title: String, description: String, dueAt: Long, onSaved: () -> Unit) {
        viewModelScope.launch {
            val existing = if (taskId == NEW_TASK_ID) null else repository.find(taskId)
            if (existing == null) {
                repository.insert(Task(title = title, description = description, dueAt = dueAt))
            } else {
                repository.update(existing.copy(title = title, description = description, dueAt = dueAt))
            }
            onSaved()
        }
    }

    fun setDone(task: Task, done: Boolean) {
        viewModelScope.launch { repository.update(task.copy(done = done)) }
    }

    fun delete(task: Task) {
        viewModelScope.launch { repository.delete(task) }
    }

    companion object {
        const val NEW_TASK_ID = -1L
    }
}
