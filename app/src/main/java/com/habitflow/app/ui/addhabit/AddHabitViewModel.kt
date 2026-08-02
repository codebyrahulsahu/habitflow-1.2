package com.habitflow.app.ui.addhabit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitflow.app.data.Habit
import com.habitflow.app.data.HabitRepository
import kotlinx.coroutines.launch

class AddHabitViewModel(private val repository: HabitRepository) : ViewModel() {

    suspend fun loadHabit(id: Long): Habit? = repository.getHabit(id)

    fun saveHabit(
        existingId: Long?,
        name: String,
        emoji: String,
        frequency: String,
        reminderHour: Int?,
        reminderMinute: Int?,
        onSaved: (Long) -> Unit
    ) {
        viewModelScope.launch {
            val savedId = if (existingId != null) {
                repository.updateHabit(
                    Habit(
                        id = existingId,
                        name = name,
                        emoji = emoji,
                        frequency = frequency,
                        reminderHour = reminderHour,
                        reminderMinute = reminderMinute
                    )
                )
                existingId
            } else {
                repository.addHabit(
                    Habit(
                        name = name,
                        emoji = emoji,
                        frequency = frequency,
                        reminderHour = reminderHour,
                        reminderMinute = reminderMinute
                    )
                )
            }
            onSaved(savedId)
        }
    }

    fun deleteHabit(habit: Habit, onDeleted: () -> Unit) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
            onDeleted()
        }
    }
}
