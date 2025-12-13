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
    private val CHALLENGE_RATE = 1.10

    init {
        loadHabitsForReview()
    }

    private fun loadHabitsForReview() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            repo.getUserHabits(userId).onSuccess { allHabits ->
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

    fun calculateAndSetNextTarget(habit: Habit) {
        viewModelScope.launch {
            _uiState.update { it.copy(statusMessage = "Reviewing ${habit.name}...") }

            val currentGoal = habit.goal ?: return@launch

            val thirtyDaysAgo = LocalDate.now().minusDays(30).format(dateFormatter)

            val historyResult = repo.getHabitValueHistory(userId, habit.habitId, thirtyDaysAgo)

            historyResult.onSuccess { dailyValues ->
                if (dailyValues.size < 7) {
                    _uiState.update { it.copy(statusMessage = "${habit.name}: Insufficient data to set new goal.") }
                    return@onSuccess
                }

                val averageDailyAchieved = dailyValues.average()

                val newTarget = (averageDailyAchieved * CHALLENGE_RATE).roundToInt()

                val minimumViableTarget = (currentGoal * 1.05).roundToInt()

                val finalGoal = maxOf(newTarget, minimumViableTarget)

                if (finalGoal == currentGoal) {
                    _uiState.update { it.copy(statusMessage = "${habit.name}: Goal maintained at $finalGoal.") }
                    return@onSuccess
                }

                repo.updateHabitGoal(userId, habit.habitId, finalGoal).onSuccess {
                    _uiState.update { it.copy(statusMessage = "${habit.name}: Goal successfully updated to $finalGoal!") }
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