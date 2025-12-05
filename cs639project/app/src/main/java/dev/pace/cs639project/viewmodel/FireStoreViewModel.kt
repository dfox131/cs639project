package dev.pace.cs639project.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.pace.cs639project.data.FirestoreRepository
import dev.pace.cs639project.data.Habit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FirestoreViewModel(
    private val repo: FirestoreRepository = FirestoreRepository()
) : ViewModel() {

    // STATE: list of habits
    private val _habits = MutableStateFlow<List<Habit>>(emptyList())
    val habits = _habits.asStateFlow()

    // ----------------------------
    // LOAD HABITS
    // ----------------------------
    fun loadHabits(userId: String) {
        viewModelScope.launch {
            val result = repo.getUserHabits(userId)

            result.onSuccess { listOfMaps ->
                _habits.value = listOfMaps.mapNotNull { map ->
                    Habit.fromMap(map)    // convert Map → Habit object
                }
            }
        }
    }

    // ----------------------------
    // ADD HABIT
    // ----------------------------
    fun addHabit(
        userId: String,
        habit: Habit,
        onDone: () -> Unit = {}
    ) {
        viewModelScope.launch {
            val result = repo.createHabit(
                userId = userId,
                name = habit.name,
                type = habit.type,
                goal = habit.goal,
                reminderTime = habit.reminderTime
            )

            result.onSuccess {
                onDone()
                loadHabits(userId)      // refresh list
            }
        }
    }

    // ----------------------------
    // MARK HABIT COMPLETED
    // ----------------------------
    fun markCompleted(
        userId: String,
        habitId: String,
        date: String,
        value: Int? = null
    ) {
        viewModelScope.launch {
            repo.markHabitCompleted(userId, habitId, date, value)
        }
    }
}
