package com.habitflow.app.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.habitflow.app.HabitFlowApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * AlarmManager alarms are cleared when the phone reboots, so we listen for
 * BOOT_COMPLETED and re-schedule a reminder for every habit that has one set.
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val pendingResult = goAsync()
        val app = context.applicationContext as HabitFlowApp

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val habits = app.repository.allHabits.first()
                habits.forEach { habit ->
                    val hour = habit.reminderHour
                    val minute = habit.reminderMinute
                    if (hour != null && minute != null) {
                        NotificationScheduler.schedule(context, habit.id, habit.name, hour, minute)
                    }
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
