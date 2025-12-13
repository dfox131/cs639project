package dev.pace.cs639project.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.pace.cs639project.data.FirestoreRepository
import dev.pace.cs639project.ui.components.ExerciseSuggestion // Assumed import
import dev.pace.cs639project.ui.components.exerciseSuggestions // Use the list of real data
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.util.Log // Use Android logging

data class ApiSuggestionsUiState(
    val suggestions: List<ExerciseSuggestion> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class ApiSuggestionsViewModel(
    private val firestoreRepository: FirestoreRepository = FirestoreRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ApiSuggestionsUiState())
    val uiState: StateFlow<ApiSuggestionsUiState> = _uiState.asStateFlow()

    init {
        fetchSuggestions()
    }

    private fun fetchSuggestions() {

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            kotlinx.coroutines.delay(500)

            _uiState.update {
                it.copy(
                    suggestions = exerciseSuggestions,
                    isLoading = false
                )
            }
        }
    }

    fun addHabitFromSuggestion(userId: String, suggestion: ExerciseSuggestion) {
        viewModelScope.launch {
            val result = firestoreRepository.createHabit(
                userId = userId,
                name = suggestion.name,
                type = "exercise",
                goal = 1,
                reminderTime = null
            )

            result.onSuccess { habitId ->
                Log.d("ApiVM", "Habit created with ID: $habitId")
            }.onFailure { e ->
                Log.e("ApiVM", "Failed to create habit: ${e.message}")
                _uiState.update { it.copy(error = "Failed to save habit: ${e.message}") }
            }
        }
    }
}