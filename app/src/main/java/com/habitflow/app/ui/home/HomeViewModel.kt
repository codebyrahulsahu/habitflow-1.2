package com.habitflow.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitflow.app.data.Habit
import com.habitflow.app.data.HabitRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class HabitWithStatus(
    val habit: Habit,
    val completedToday: Boolean,
    val streak: Int
)

class HomeViewModel(private val repository: HabitRepository) : ViewModel() {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val today: String get() = dateFormat.format(Date())

    val habitList: StateFlow<List<HabitWithStatus>> =
        combine(repository.allHabits, repository.allCompletions) { habits, completions ->
            habits.map { habit ->
                val habitDates = completions.filter { it.habitId == habit.id }
                    .map { it.date }
                    .toSet()
                HabitWithStatus(
                    habit = habit,
                    completedToday = today in habitDates,
                    streak = calculateStreak(habitDates)
                )
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Counts consecutive completed days walking backwards from today. */
    private fun calculateStreak(dates: Set<String>): Int {
        var streak = 0
        val cal = Calendar.getInstance()
        while (true) {
            val dateStr = dateFormat.format(cal.time)
            if (dateStr in dates) {
                streak++
                cal.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                break
            }
        }
        return streak
    }

    fun toggleToday(habitId: Long) {
        viewModelScope.launch {
            repository.toggleCompletion(habitId, today)
        }
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
        }
    }
}
