package com.habitflow.app.data

import kotlinx.coroutines.flow.Flow

/**
 * Single source of truth for habit data. ViewModels talk to this class,
 * never directly to Room — makes it easy to swap storage later if needed.
 */
class HabitRepository(private val dao: HabitDao) {

    val allHabits: Flow<List<Habit>> = dao.getAllHabits()
    val allCompletions: Flow<List<HabitCompletion>> = dao.getAllCompletions()

    suspend fun addHabit(habit: Habit): Long = dao.insertHabit(habit)

    suspend fun updateHabit(habit: Habit) = dao.updateHabit(habit)

    suspend fun deleteHabit(habit: Habit) = dao.deleteHabit(habit)

    suspend fun getHabit(id: Long): Habit? = dao.getHabitById(id)

    /** Marks [habitId] done for [date] if not already, otherwise un-marks it. */
    suspend fun toggleCompletion(habitId: Long, date: String) {
        val alreadyDone = dao.isCompletedOn(habitId, date) > 0
        if (alreadyDone) {
            dao.deleteCompletion(habitId, date)
        } else {
            dao.insertCompletion(HabitCompletion(habitId = habitId, date = date))
        }
    }
}
