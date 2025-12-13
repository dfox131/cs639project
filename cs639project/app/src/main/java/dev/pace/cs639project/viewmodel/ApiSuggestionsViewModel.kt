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

// --- UI State for API Screen ---
data class ApiSuggestionsUiState(
    val suggestions: List<ExerciseSuggestion> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

// --- ViewModel ---
class ApiSuggestionsViewModel(
    private val firestoreRepository: FirestoreRepository = FirestoreRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ApiSuggestionsUiState())
    val uiState: StateFlow<ApiSuggestionsUiState> = _uiState.asStateFlow()

    init {
        fetchSuggestions()
    }

    private fun fetchSuggestions() {
        // In a real app, this would call an API repository (e.g., using Retrofit).
        // For now, use the local list of real exercises.
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // Simulate network delay
            kotlinx.coroutines.delay(500)

            _uiState.update {
                it.copy(
                    suggestions = exerciseSuggestions, // Use the real data list
                    isLoading = false
                )
            }
        }
    }

    /**
     * 🔥 FIX: Saves the selected exercise suggestion as a new habit in Firestore.
     */
    fun addHabitFromSuggestion(userId: String, suggestion: ExerciseSuggestion) {
        viewModelScope.launch {
            // Default parameters for exercise habits
            val result = firestoreRepository.createHabit(
                userId = userId,
                name = suggestion.name,
                type = "exercise",
                goal = 1, // Default goal: 1 completion per day
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