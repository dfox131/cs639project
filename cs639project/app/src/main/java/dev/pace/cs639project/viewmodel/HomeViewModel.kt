package dev.pace.cs639project.viewmodel

import androidx.lifecycle.ViewModel
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

data class HomeUiState(
    val userName: String = "User",
    val allHabits: List<Habit> = emptyList(),
    val completedHabitIds: Set<String> = emptySet(),
    val totalHabitsCount: Int = 0,
    val completedCount: Int = 0,
    val isLoading: Boolean = true,
    val error: String? = null
)

class HomeViewModel(
    private val repo: FirestoreRepository = FirestoreRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    fun loadDailyData(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val todayDate = LocalDate.now().format(dateFormatter)

            val habitsResult = repo.getUserHabits(userId)

            habitsResult.onSuccess { habits ->

                val progressResult = repo.getTodayProgress(userId, todayDate)

                progressResult.onSuccess { progressList ->

                    val completedIds = progressList
                        .mapNotNull { it["habitId"] as? String }
                        .toSet()

                    _uiState.update {
                        it.copy(
                            allHabits = habits,
                            completedHabitIds = completedIds,
                            totalHabitsCount = habits.size,
                            completedCount = completedIds.size,
                            isLoading = false
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

            }.onFailure { e ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load habits: ${e.message}"
                    )
                }
            }
        }
    }
}
