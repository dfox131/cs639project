package dev.pace.cs639project.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.pace.cs639project.data.FirestoreRepository
import dev.pace.cs639project.data.Habit // Assuming Habit is available in data package
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

// --- Data Structures ---

/**
 * Represents the entire UI state for the Streak Tracker Screen.
 */
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
    private val repo: FirestoreRepository = FirestoreRepository(),
    private val userId: String = "default_user_id"
) : ViewModel() {

    private val _uiState = MutableStateFlow(StreakTrackerUiState())
    val uiState: StateFlow<StreakTrackerUiState> = _uiState.asStateFlow()

    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE // YYYY-MM-DD

    init {
        loadHabitAndProgress()
    }

    /**
     * Loads the habit details and its entire progress history.
     */
    private fun loadHabitAndProgress() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val habitResult = repo.getUserHabits(userId)

            // --- 1. Load Habit Name ---
            habitResult.onSuccess { habitsList ->
                // Find the specific habit we are tracking using the habitId
                val habit = habitsList.find { it.habitId == habitId }

                if (habit != null) {
                    _uiState.update { it.copy(habitName = habit.name) }
                } else {
                    _uiState.update { it.copy(error = "Habit with ID $habitId not found for user.") }
                }
            }.onFailure { e ->
                _uiState.update { it.copy(error = "Failed to load habit details: ${e.message}") }
            }


            // --- 2. Fetch History & Calculate Streak ---
            val progressResult = repo.getHabitProgressHistory(userId, habitId)

            progressResult.onSuccess { history ->
                // Extract and format the completed dates
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
                    .toSet()
                    .filter { it.isBefore(LocalDate.now().plusDays(1)) }
                    .toSet()


                // 3. Calculate Streak and Weekly Stats
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
     * Logic: Starts at the current day and checks backwards for consecutive completion.
     */
    private fun calculateCurrentStreak(completedDates: Set<LocalDate>): Int {
        var streak = 0
        var checkDate = LocalDate.now()

        // If today is completed, start with 1 and check backwards from yesterday.
        if (completedDates.contains(checkDate)) {
            streak = 1
            checkDate = checkDate.minusDays(1)
        } else {
            // If today is missed, start checking backwards from yesterday.
            checkDate = checkDate.minusDays(1)
        }

        // Check for consecutive days backwards
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
            // Only consider completions within the last 7 days (including today) for the weekly tracker
            if (date.isAfter(lastWeek) && !date.isAfter(today)) {
                completedDays.add(date.dayOfWeek)
            }
        }
        return completedDays
    }


    companion object {
        fun Factory(habitId: String): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return StreakTrackerViewModel(habitId) as T
                }
            }
    }
}