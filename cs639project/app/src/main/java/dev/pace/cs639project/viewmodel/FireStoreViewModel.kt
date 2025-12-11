package dev.pace.cs639project.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.pace.cs639project.data.AuthRepository
import dev.pace.cs639project.data.FirestoreRepository
import dev.pace.cs639project.data.Habit
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class FirestoreViewModel(
    private val repo: FirestoreRepository = FirestoreRepository()
) : ViewModel() {

    private val authRepo = AuthRepository()

    // ----------------------------
    // AUTH STATE
    // ----------------------------
    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId.asStateFlow()

    private val _habitCompleted = MutableSharedFlow<String>(replay = 0)
    val habitCompleted = _habitCompleted.asSharedFlow()

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
            result.onSuccess { habitsList ->
                _habits.value = habitsList
            }
        }
    }


    // ----------------------------
    // ADD HABIT
    // ----------------------------
    fun addHabit(
        userId: String,
        habit: Habit,
        onDone: () -> Unit
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
                loadHabits(userId) // reload list
                onDone()
            }
        }
    }

    // ----------------------------
    // MARK HABIT COMPLETED
    // ----------------------------
    fun markHabitCompleted(userId: String, habitId: String, value: Int? = null) {
        viewModelScope.launch {
            val today = LocalDate.now().toString()
            val result = repo.markHabitCompleted(userId, habitId, today, value)

            result.onSuccess {
                _habitCompleted.emit(habitId)
            }
        }
    }

}
