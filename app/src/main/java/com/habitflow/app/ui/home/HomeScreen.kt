package com.habitflow.app.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.habitflow.app.data.HabitRepository
import com.habitflow.app.ui.GenericViewModelFactory

@Composable
fun HomeScreen(
    repository: HabitRepository,
    onAddHabit: () -> Unit,
    onEditHabit: (Long) -> Unit
) {
    val viewModel: HomeViewModel = viewModel(
        factory = GenericViewModelFactory { HomeViewModel(repository) }
    )
    val habits by viewModel.habitList.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("HabitFlow") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddHabit) {
                Icon(Icons.Filled.Add, contentDescription = "Add habit")
            }
        }
    ) { padding ->
        if (habits.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No habits yet. Tap + to add your first habit.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(habits, key = { it.habit.id }) { item ->
                    HabitRow(
                        item = item,
                        onToggle = { viewModel.toggleToday(item.habit.id) },
                        onClick = { onEditHabit(item.habit.id) },
                        onDelete = { viewModel.deleteHabit(item.habit) }
                    )
                }
            }
        }
    }
}

@Composable
private fun HabitRow(
    item: HabitWithStatus,
    onToggle: () -> Unit,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    ElevatedCard(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(14.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(item.habit.emoji, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.habit.name, style = MaterialTheme.typography.titleMedium)
                Text("🔥 ${item.streak} day streak", style = MaterialTheme.typography.labelMedium)
            }
            Checkbox(checked = item.completedToday, onCheckedChange = { onToggle() })
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete habit")
            }
        }
    }
}
