package com.habitflow.app.ui.addhabit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.habitflow.app.data.Habit
import com.habitflow.app.data.HabitRepository
import com.habitflow.app.notification.NotificationScheduler
import com.habitflow.app.ui.GenericViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHabitScreen(
    repository: HabitRepository,
    habitId: Long?,
    onDone: () -> Unit
) {
    val viewModel: AddHabitViewModel = viewModel(
        factory = GenericViewModelFactory { AddHabitViewModel(repository) }
    )
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var emoji by remember { mutableStateOf("✅") }
    var frequency by remember { mutableStateOf("DAILY") }
    var reminderHour by remember { mutableStateOf<Int?>(null) }
    var reminderMinute by remember { mutableStateOf<Int?>(null) }
    var showTimePicker by remember { mutableStateOf(false) }
    var existingHabit by remember { mutableStateOf<Habit?>(null) }

    LaunchedEffect(habitId) {
        if (habitId != null) {
            viewModel.loadHabit(habitId)?.let { habit ->
                existingHabit = habit
                name = habit.name
                emoji = habit.emoji
                frequency = habit.frequency
                reminderHour = habit.reminderHour
                reminderMinute = habit.reminderMinute
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(if (habitId == null) "New Habit" else "Edit Habit") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = emoji,
                onValueChange = { if (it.length <= 2) emoji = it },
                label = { Text("Icon / Emoji") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Habit name") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Frequency", style = MaterialTheme.typography.labelLarge)
            Row {
                listOf("DAILY" to "Daily", "WEEKLY" to "Weekly").forEach { (value, label) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .selectable(selected = frequency == value, onClick = { frequency = value })
                            .padding(end = 16.dp)
                    ) {
                        RadioButton(selected = frequency == value, onClick = { frequency = value })
                        Text(label)
                    }
                }
            }

            OutlinedButton(onClick = { showTimePicker = true }) {
                val hour = reminderHour
                val minute = reminderMinute
                Text(
                    if (hour != null && minute != null) {
                        "Reminder: %02d:%02d".format(hour, minute)
                    } else {
                        "Set reminder (optional)"
                    }
                )
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = {
                    val finalName = name.ifBlank { "Untitled habit" }
                    viewModel.saveHabit(
                        existingId = habitId,
                        name = finalName,
                        emoji = emoji.ifBlank { "✅" },
                        frequency = frequency,
                        reminderHour = reminderHour,
                        reminderMinute = reminderMinute
                    ) { savedId ->
                        val hour = reminderHour
                        val minute = reminderMinute
                        if (hour != null && minute != null) {
                            NotificationScheduler.schedule(context, savedId, finalName, hour, minute)
                        } else {
                            NotificationScheduler.cancel(context, savedId)
                        }
                        onDone()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Save") }

            existingHabit?.let { habit ->
                OutlinedButton(
                    onClick = {
                        viewModel.deleteHabit(habit) {
                            NotificationScheduler.cancel(context, habit.id)
                            onDone()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Delete habit") }
            }
        }
    }

    if (showTimePicker) {
        val timeState = rememberTimePickerState(
            initialHour = reminderHour ?: 9,
            initialMinute = reminderMinute ?: 0,
            is24Hour = true
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    reminderHour = timeState.hour
                    reminderMinute = timeState.minute
                    showTimePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Cancel") }
            },
            text = { TimePicker(state = timeState) }
        )
    }
}
