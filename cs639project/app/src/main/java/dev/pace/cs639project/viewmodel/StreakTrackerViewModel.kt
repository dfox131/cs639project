package dev.pace.cs639project.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.pace.cs639project.data.FirestoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// --- UI State ---
data class StreakTrackerUiState(
    val habitName: String? = "Loading Habit...",
    val currentStreak: Int = 0,
    val completedDates: Set<LocalDate> = emptySet(),
    val weeklyCompletionDays: Set<DayOfWeek> = emptySet(),
    val isLoading: Boolean = true,
    val error: String? = null
)

// --- ViewModel ---
class StreakTrackerViewModel(
    private val habitId: String,
    private val userId: String,
    private val repo: FirestoreRepository = FirestoreRepository(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(StreakTrackerUiState())
    val uiState: StateFlow<StreakTrackerUiState> = _uiState.asStateFlow()

    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE // YYYY-MM-DD

    init {
        // as soon as the VM is created (with a real userId), load data
        loadHabitAndProgress()
    }

    /**
     * Loads the habit details and its entire progress history.
     */
    private fun loadHabitAndProgress() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // --- 1. Load Habits for this user ---
            val habitResult = repo.getUserHabits(userId)

            habitResult.onSuccess { habitsList ->
                val habit = habitsList.find { it.habitId == habitId }

                if (habit != null) {
                    _uiState.update { it.copy(habitName = habit.name) }
                } else {
                    _uiState.update {
                        it.copy(error = "Habit with ID $habitId not found for user.")
                    }
                    return@launch
                }

            }.onFailure { e ->
                _uiState.update {
                    it.copy(error = "Failed to load habit details: ${e.message}")
                }
                return@launch
            }

            // --- 2. Load Progress for this user + habit ---
            val progressResult = repo.getHabitProgressHistory(userId, habitId)

            progressResult.onSuccess { history ->

                val completedDates = history
                    .mapNotNull { data ->
                        (data["date"] as? String)?.let { dateString ->
                            try {
                                LocalDate.parse(dateString, dateFormatter)
                            } catch (_: Exception) {
                                null
                            }
                        }
                    }
                    .filter { it.isBefore(LocalDate.now().plusDays(1)) }
                    .toSet()

                val streak = calculateCurrentStreak(completedDates)
                val weeklyDays = calculateWeeklyCompletion(completedDates)

                _uiState.update {
                    it.copy(
                        completedDates = completedDates,
                        currentStreak = streak,
                        weeklyCompletionDays = weeklyDays,
                        isLoading = false,
                        error = null
                    )
                }

            }.onFailure { e ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load progress: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Calculates the longest consecutive streak ending yesterday or today.
     */
    private fun calculateCurrentStreak(completedDates: Set<LocalDate>): Int {
        var streak = 0
        var checkDate = LocalDate.now()

        if (completedDates.contains(checkDate)) {
            streak = 1
            checkDate = checkDate.minusDays(1)
        } else {
            checkDate = checkDate.minusDays(1)
        }

        while (completedDates.contains(checkDate)) {
            streak++
            checkDate = checkDate.minusDays(1)
        }

        return streak
    }

    /**
     * Determines which days of the week have completions in the last 7 days.
     */
    private fun calculateWeeklyCompletion(completedDates: Set<LocalDate>): Set<DayOfWeek> {
        val completedDays = mutableSetOf<DayOfWeek>()
        val today = LocalDate.now()
        val lastWeek = today.minusDays(7)

        completedDates.forEach { date ->
            if (date.isAfter(lastWeek) && !date.isAfter(today)) {
                completedDays.add(date.dayOfWeek)
            }
        }
        return completedDays
    }

    companion object {
        fun Factory(habitId: String, userId: String): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return StreakTrackerViewModel(
                        habitId = habitId,
                        userId = userId
                    ) as T
                }
            }
    }
}
