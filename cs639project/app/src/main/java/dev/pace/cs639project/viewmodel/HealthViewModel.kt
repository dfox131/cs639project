package dev.pace.cs639project.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.health.connect.client.HealthConnectClient
import dev.pace.cs639project.data.HealthApiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

// --- UI State for Health Data ---
data class HealthUiState(
    val stepsToday: Long = 0L,
    val isLoading: Boolean = false,
    val permissionsGranted: Boolean = false,
    val error: String? = null,
    val permissionsRequired: Boolean = false
)

class HealthViewModel(
    private val healthRepo: HealthApiRepository,
    private val healthConnectClient: HealthConnectClient // Client passed for permission management
) : ViewModel() {

    private val _uiState = MutableStateFlow(HealthUiState())
    val uiState: StateFlow<HealthUiState> = _uiState.asStateFlow()

    init {
        // Check permission status immediately on creation
        checkPermissionsAndLoadSteps()
    }

    /**
     * Checks current permissions and initiates data load if authorized.
     */
    fun checkPermissionsAndLoadSteps() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val permissionsGranted = healthRepo.arePermissionsGranted()

            if (permissionsGranted) {
                _uiState.update { it.copy(permissionsGranted = true) }
                // Proceed to load data if permissions are OK
                loadTodaySteps()
            } else {
                _uiState.update {
                    it.copy(
                        permissionsGranted = false,
                        permissionsRequired = true,
                        isLoading = false
                    )
                }
            }
        }
    }

    /**
     * Loads the step count for the current day from the Health Connect API.
     */
    private fun loadTodaySteps() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val steps = healthRepo.getStepsForDay(LocalDate.now())

                _uiState.update {
                    it.copy(
                        stepsToday = steps,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load steps: ${e.message}"
                    )
                }
            }
        }
    }

    // --- Permissions Handling ---

    // NOTE: The actual permission launcher (Activity result logic) must be handled
    // in the Composable (UI) layer, as it requires an Activity context.

    fun permissionsRequestCompleted() {
        // This function is called by the UI layer after the user returns from the permission screen.
        checkPermissionsAndLoadSteps()
    }


    // --- ViewModel Factory ---

    companion object {
        // This factory is necessary to instantiate the ViewModel with dependencies (Context, Client)
        fun Factory(context: Context): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    // Initialize the repository and client here
                    val healthConnectClient = HealthConnectClient.getOrCreate(context)
                    val healthRepo = HealthApiRepository(context, healthConnectClient)

                    return HealthViewModel(healthRepo, healthConnectClient) as T
                }
            }
    }
}