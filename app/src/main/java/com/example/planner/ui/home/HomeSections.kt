package com.example.planner.ui.home

import com.example.planner.data.Task

const val UPCOMING_LIMIT = 10

// Why: what Home shows, split out as plain data so the rules can be unit tested without Android.
data class HomeSections(
    val overdue: List<Task>,
    val next: Task?,
    val upcoming: List<Task>
) {
    val isEmpty: Boolean get() = overdue.isEmpty() && next == null
}

// Why: Home only shows open work. A task due exactly now is not overdue yet (overdue means strictly before now);
// it becomes the "Volgende" card. Everything is sorted by due time, regardless of the input order.
fun splitTasks(tasks: List<Task>, now: Long): HomeSections {
    val open = tasks.filter { !it.done }.sortedBy { it.dueAt }
    val (overdue, notOverdue) = open.partition { it.dueAt < now }
    return HomeSections(
        overdue = overdue,
        next = notOverdue.firstOrNull(),
        upcoming = notOverdue.drop(1).take(UPCOMING_LIMIT)
    )
}
