package com.habitflow.app.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitflow.app.data.Habit
import com.habitflow.app.data.HabitRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class CalendarState(
    val habits: List<Habit> = emptyList(),
    val completionsByDate: Map<String, List<Long>> = emptyMap()
)

class CalendarViewModel(repository: HabitRepository) : ViewModel() {

    val state: StateFlow<CalendarState> =
        combine(repository.allHabits, repository.allCompletions) { habits, completions ->
            CalendarState(
                habits = habits,
                completionsByDate = completions.groupBy({ it.date }, { it.habitId })
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CalendarState())
}
