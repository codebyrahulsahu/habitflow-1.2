package com.habitflow.app.notification

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.habitflow.app.HabitFlowApp
import com.habitflow.app.MainActivity

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val habitId = intent.getLongExtra(EXTRA_HABIT_ID, -1)
        val habitName = intent.getStringExtra(EXTRA_HABIT_NAME) ?: "Habit"

        val openAppIntent = Intent(context, MainActivity::class.java)
        val contentIntent = PendingIntent.getActivity(
            context,
            habitId.toInt(),
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, HabitFlowApp.REMINDER_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Time for: $habitName")
            .setContentText("Don't break your streak — mark it done in HabitFlow.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(contentIntent)
            .setAutoCancel(true)
            .build()

        // POST_NOTIFICATIONS may not be granted on Android 13+; fail silently if so.
        try {
            NotificationManagerCompat.from(context).notify(habitId.toInt(), notification)
        } catch (e: SecurityException) {
            // Permission not granted — nothing we can do here, user can grant it in Settings.
        }
    }

    companion object {
        const val EXTRA_HABIT_ID = "habit_id"
        const val EXTRA_HABIT_NAME = "habit_name"
    }
}
