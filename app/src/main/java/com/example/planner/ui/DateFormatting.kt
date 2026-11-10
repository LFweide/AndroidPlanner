package com.example.planner.ui

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

// Why: tasks store epoch millis; screens need readable local text. One place for the conversion.
private val dateFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
private val timeFormat = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)

fun toLocal(millis: Long): ZonedDateTime = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault())

fun formatDate(millis: Long): String = toLocal(millis).format(dateFormat)

fun formatTime(millis: Long): String = toLocal(millis).format(timeFormat)

fun formatDateTime(millis: Long): String = "${formatDate(millis)} ${formatTime(millis)}"
