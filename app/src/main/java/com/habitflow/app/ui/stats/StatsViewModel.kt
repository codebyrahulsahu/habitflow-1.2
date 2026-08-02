package com.habitflow.app.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitflow.app.data.Habit
import com.habitflow.app.data.HabitRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class HabitStat(
    val habit: Habit,
    val weeklyPercent: Int,
    val monthlyPercent: Int
)

class StatsViewModel(repository: HabitRepository) : ViewModel() {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    val stats: StateFlow<List<HabitStat>> =
        combine(repository.allHabits, repository.allCompletions) { habits, completions ->
            habits.map { habit ->
                val habitDates = completions.filter { it.habitId == habit.id }
                    .map { it.date }
                    .toSet()
                HabitStat(
                    habit = habit,
                    weeklyPercent = percentCompleted(habitDates, 7),
                    monthlyPercent = percentCompleted(habitDates, 30)
                )
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private fun percentCompleted(dates: Set<String>, windowDays: Int): Int {
        val cal = Calendar.getInstance()
        var completed = 0
        repeat(windowDays) {
            val dateStr = dateFormat.format(cal.time)
            if (dateStr in dates) completed++
            cal.add(Calendar.DAY_OF_YEAR, -1)
        }
        return ((completed.toFloat() / windowDays) * 100).toInt()
    }
}
