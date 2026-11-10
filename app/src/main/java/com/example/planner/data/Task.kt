package com.example.planner.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// Why: one row in the "tasks" table. dueAt is epoch millis so sorting by time is a plain number sort.
@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val dueAt: Long,
    val done: Boolean = false,
    val contactName: String? = null,
    val reminder: Boolean = false
)
