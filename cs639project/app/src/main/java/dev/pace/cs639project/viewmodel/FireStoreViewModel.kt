package dev.pace.cs639project.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.pace.cs639project.data.AuthRepository
import dev.pace.cs639project.data.FirestoreRepository
import dev.pace.cs639project.data.Habit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FirestoreViewModel(
    private val repo: FirestoreRepository = FirestoreRepository()
) : ViewModel() {

    private val authRepo = AuthRepository()

    // ----------------------------
    // AUTH STATE
    // ----------------------------
    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId.asStateFlow()

    init {
        initAuth()
    }

    fun initAuth() {
        viewModelScope.launch {
            println("AUTH STARTED")

            val current = authRepo.getCurrentUserId()

            if (current != null) {
                _userId.value = current
            } else {
                val result = authRepo.signInAnonymously()

                result.onSuccess { uid ->
                    println("AUTH SUCCESS: $uid")
                    _userId.value = uid
                }

                result.onFailure {
                    it.printStackTrace()
                }
            }
        }
    }

    // ----------------------------
    // HABIT STATE
    // ----------------------------
    private val _habits = MutableStateFlow<List<Habit>>(emptyList())
    val habits = _habits.asStateFlow()

    // ----------------------------
    // LOAD HABITS
    // ----------------------------
    fun loadHabits(userId: String) {
        viewModelScope.launch {
            val result = repo.getUserHabits(userId)

            result
                .onSuccess { habits ->
                    _habits.value = habits
                }
                .onFailure { e ->
                    e.printStackTrace()
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
                loadHabits(userId)
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
