package com.habitflow.app.ui.calendar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.habitflow.app.data.HabitRepository
import com.habitflow.app.ui.GenericViewModelFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun CalendarScreen(repository: HabitRepository) {
    val viewModel: CalendarViewModel = viewModel(
        factory = GenericViewModelFactory { CalendarViewModel(repository) }
    )
    val state by viewModel.state.collectAsState()

    var selectedDate by remember { mutableStateOf<String?>(null) }

    val today = Calendar.getInstance()
    val year = today.get(Calendar.YEAR)
    val month = today.get(Calendar.MONTH)
    val monthFormat = remember { SimpleDateFormat("MMMM yyyy", Locale.getDefault()) }
    val dayFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US) }

    val firstOfMonth = Calendar.getInstance().apply { set(year, month, 1) }
    val daysInMonth = firstOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
    val startOffset = firstOfMonth.get(Calendar.DAY_OF_WEEK) - 1 // 0 = Sunday

    Scaffold(topBar = { TopAppBar(title = { Text(monthFormat.format(firstOfMonth.time)) }) }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(12.dp).fillMaxSize()) {
            LazyVerticalGrid(columns = GridCells.Fixed(7), modifier = Modifier.fillMaxWidth()) {
                items(startOffset) { Box(Modifier.aspectRatio(1f)) }

                items(daysInMonth) { dayIndex ->
                    val day = dayIndex + 1
                    val cal = Calendar.getInstance().apply { set(year, month, day) }
                    val dateStr = dayFormat.format(cal.time)
                    val completedCount = state.completionsByDate[dateStr]?.size ?: 0

                    Box(
                        modifier = Modifier.aspectRatio(1f).padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            onClick = { selectedDate = dateStr },
                            shape = MaterialTheme.shapes.small,
                            color = if (completedCount > 0) {
                                MaterialTheme.colorScheme.primary.copy(
                                    alpha = (0.25f + 0.15f * completedCount).coerceAtMost(1f)
                                )
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            }
                        ) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(day.toString(), style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }

            selectedDate?.let { date ->
                val completedIds = state.completionsByDate[date].orEmpty().toSet()
                val completedHabits = state.habits.filter { it.id in completedIds }

                Spacer(Modifier.height(16.dp))
                Text(date, style = MaterialTheme.typography.titleMedium)
                if (completedHabits.isEmpty()) {
                    Text("No habits completed this day.")
                } else {
                    completedHabits.forEach { habit ->
                        Text("${habit.emoji} ${habit.name}")
                    }
                }
            }
        }
    }
}
