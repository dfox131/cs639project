package dev.pace.cs639project.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.pace.cs639project.data.FirestoreRepository
import dev.pace.cs639project.data.Habit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

data class GoalReviewUiState(
    val habits: List<Habit> = emptyList(),
    val habitsToReview: List<Habit> = emptyList(),
    val isLoading: Boolean = true,
    val statusMessage: String? = null,
    val error: String? = null
)

class HabitGoalsViewModel(
    private val userId: String,
    private val repo: FirestoreRepository = FirestoreRepository(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(GoalReviewUiState())
    val uiState: StateFlow<GoalReviewUiState> = _uiState.asStateFlow()

    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    private val CHALLENGE_RATE = 1.10 // 10% increase

    init {
        loadHabitsForReview()
    }

    private fun loadHabitsForReview() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            repo.getUserHabits(userId).onSuccess { allHabits ->
                // Filter for habits that are quantifiable (goal is set)
                val reviewableHabits = allHabits.filter { it.goal != null && it.goal > 0 }

                _uiState.update {
                    it.copy(
                        habits = allHabits,
                        habitsToReview = reviewableHabits,
                        isLoading = false
                    )
                }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    /**
     * Calculates the new incremental goal and updates it in Firestore.
     */
    fun calculateAndSetNextTarget(habit: Habit) {
        viewModelScope.launch {
            _uiState.update { it.copy(statusMessage = "Reviewing ${habit.name}...") }

            val currentGoal = habit.goal ?: return@launch

            // 1. Define the analysis period
            val thirtyDaysAgo = LocalDate.now().minusDays(30).format(dateFormatter)

            // 2. Fetch the actual daily values achieved in the last 30 days
            val historyResult = repo.getHabitValueHistory(userId, habit.habitId, thirtyDaysAgo)

            historyResult.onSuccess { dailyValues ->
                if (dailyValues.size < 7) {
                    _uiState.update { it.copy(statusMessage = "${habit.name}: Insufficient data to set new goal.") }
                    return@onSuccess
                }

                // 3. Calculate the baseline (average daily value achieved)
                val averageDailyAchieved = dailyValues.average()

                // 4. Calculate the new target based on the baseline
                val newTarget = (averageDailyAchieved * CHALLENGE_RATE).roundToInt()

                // 5. Ensure the new goal is at least 5% higher than the OLD goal
                val minimumViableTarget = (currentGoal * 1.05).roundToInt()

                // Set the final goal: ensures progress but doesn't punish underperformance too harshly
                val finalGoal = maxOf(newTarget, minimumViableTarget)

                if (finalGoal == currentGoal) {
                    _uiState.update { it.copy(statusMessage = "${habit.name}: Goal maintained at $finalGoal.") }
                    return@onSuccess
                }

                // 6. Update the habit document in Firestore
                repo.updateHabitGoal(userId, habit.habitId, finalGoal).onSuccess {
                    _uiState.update { it.copy(statusMessage = "${habit.name}: Goal successfully updated to $finalGoal!") }
                    // Reload habits to reflect change in UI if necessary
                    loadHabitsForReview()
                }.onFailure { e ->
                    _uiState.update { it.copy(error = "Failed to save goal for ${habit.name}: ${e.message}") }
                }

            }.onFailure { e ->
                _uiState.update { it.copy(error = "Failed to fetch history for ${habit.name}: ${e.message}") }
            }
        }
    }
    fun clearStatusMessage() {
        // Must be called by the UI after the Snackbar is shown
        _uiState.update { it.copy(statusMessage = null) }
    }

    companion object {
        fun Factory(userId: String): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return HabitGoalsViewModel(userId = userId) as T
                }
            }
    }
}