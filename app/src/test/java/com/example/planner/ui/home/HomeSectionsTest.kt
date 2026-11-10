package com.example.planner.ui.home

import com.example.planner.data.Task
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeSectionsTest {
    private val now = 1_000L

    private fun task(id: Long, dueAt: Long, done: Boolean = false) =
        Task(id = id, title = "t$id", dueAt = dueAt, done = done)

    @Test
    fun emptyList_givesEmptySections() {
        val result = splitTasks(emptyList(), now)
        assertTrue(result.isEmpty)
        assertTrue(result.overdue.isEmpty())
        assertNull(result.next)
        assertTrue(result.upcoming.isEmpty())
    }

    @Test
    fun onlyDoneTasks_givesEmptySections() {
        assertTrue(splitTasks(listOf(task(1, 500, done = true), task(2, 2_000, done = true)), now).isEmpty)
    }

    @Test
    fun dueEqualsNow_isNextAndNotOverdue() {
        val result = splitTasks(listOf(task(1, now)), now)
        assertTrue(result.overdue.isEmpty())
        assertEquals(1L, result.next?.id)
    }

    @Test
    fun oneMillisecondBeforeNow_isOverdue() {
        val result = splitTasks(listOf(task(1, now - 1)), now)
        assertEquals(listOf(1L), result.overdue.map { it.id })
        assertNull(result.next)
    }

    @Test
    fun overdueAreSortedAndSeparateFromNext() {
        val result = splitTasks(listOf(task(3, 3_000), task(2, 800), task(1, 100)), now)
        assertEquals(listOf(1L, 2L), result.overdue.map { it.id })
        assertEquals(3L, result.next?.id)
        assertTrue(result.upcoming.isEmpty())
    }

    @Test
    fun upcomingIsCappedAtTenAfterNext() {
        val tasks = (1L..15L).map { task(it, now + it) }.shuffled()
        val result = splitTasks(tasks, now)
        assertEquals(1L, result.next?.id)
        assertEquals((2L..11L).toList(), result.upcoming.map { it.id })
    }

    @Test
    fun exactlyElevenFutureTasks_fillNextPlusTen() {
        val result = splitTasks((1L..11L).map { task(it, now + it) }, now)
        assertEquals(10, result.upcoming.size)
    }

    @Test
    fun doneTasksAreIgnored() {
        val result = splitTasks(listOf(task(1, 500, done = true), task(2, 2_000), task(3, 3_000, done = true)), now)
        assertTrue(result.overdue.isEmpty())
        assertEquals(2L, result.next?.id)
        assertTrue(result.upcoming.isEmpty())
    }
}
