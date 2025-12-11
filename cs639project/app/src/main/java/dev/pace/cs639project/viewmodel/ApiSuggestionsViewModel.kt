package dev.pace.cs639project.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.pace.cs639project.ui.components.ExerciseSuggestion
import dev.pace.cs639project.ui.components.fakeSuggestions
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ApiSuggestionsUiState(
    val suggestions: List<ExerciseSuggestion> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class ApiSuggestionsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ApiSuggestionsUiState())
    val uiState: StateFlow<ApiSuggestionsUiState> = _uiState.asStateFlow()

    init {
        fetchSuggestions()
    }

    private fun fetchSuggestions() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                // Simulate network latency (e.g., fetching from an external API)
                delay(1500)

                // NOTE: In a real app, this would be a repository call (e.g., repo.getSuggestions())
                val realSuggestions = fakeSuggestions // Using fake data for now

                _uiState.update {
                    it.copy(
                        suggestions = realSuggestions,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load suggestions: ${e.message}"
                    )
                }
            }
        }
    }
}