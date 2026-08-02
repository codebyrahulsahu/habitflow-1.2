package com.habitflow.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A single habit the user wants to track (e.g. "Meditation", "Read 10 pages").
 *
 * reminderHour/reminderMinute are null when the user has not set a reminder
 * for this habit.
 */
@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val emoji: String,
    val frequency: String, // "DAILY" or "WEEKLY"
    val reminderHour: Int? = null,
    val reminderMinute: Int? = null,
    val createdAt: Long = System.currentTimeMillis()
)
