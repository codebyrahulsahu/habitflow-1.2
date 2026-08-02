package com.habitflow.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.habitflow.app.data.HabitDatabase
import com.habitflow.app.data.HabitRepository

class HabitFlowApp : Application() {

    // Simple manual dependency setup (no DI framework needed for this app size).
    val database by lazy { HabitDatabase.getInstance(this) }
    val repository by lazy { HabitRepository(database.habitDao()) }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                REMINDER_CHANNEL_ID,
                "Habit Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminders to complete your daily habits"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val REMINDER_CHANNEL_ID = "habit_reminders"
    }
}
