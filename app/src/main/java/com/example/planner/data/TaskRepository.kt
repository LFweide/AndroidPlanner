package com.example.planner.data

import kotlinx.coroutines.flow.Flow

// Why: the ViewModels talk to this instead of the DAO, so later phases (holidays, reminders)
// share one place for task data.
class TaskRepository(private val dao: TaskDao) {
    val tasks: Flow<List<Task>> = dao.observeAll()

    suspend fun find(id: Long): Task? = dao.findById(id)

    suspend fun insert(task: Task) {
        dao.insert(task)
    }

    suspend fun update(task: Task) = dao.update(task)

    suspend fun delete(task: Task) = dao.delete(task)
}
