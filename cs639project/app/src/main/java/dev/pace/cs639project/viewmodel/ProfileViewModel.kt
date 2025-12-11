package dev.pace.cs639project.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.pace.cs639project.data.FirestoreRepository
import dev.pace.cs639project.data.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// -------------------------
// UI State
// -------------------------
data class ProfileUiState(
    val email: String = "",
    val sex: String? = null,
    val height: Int? = null,
    val weight: Int? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val saveSuccess: Boolean = false
)

// -------------------------
// ViewModel
// -------------------------
class ProfileViewModel(
    private val userId: String,
    private val repo: FirestoreRepository = FirestoreRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                println("🔥 Loading profile for userId = $userId")

                val user = repo.getUserProfile(userId)

                println("🔥 Firestore returned = $user")


                if (user != null) {
                    _uiState.value = _uiState.value.copy(
                        email = user.email,
                        sex = user.sex,
                        height = user.height,
                        weight = user.weight,
                        isLoading = false,
                        saveSuccess = false,
                        error = null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        error = "Profile not found.",
                        isLoading = false
                    )
                }

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to load profile.",
                    isLoading = false
                )
            }
        }
    }


    // -------------------------
    // SAVE PROFILE BACK TO FIRESTORE
    // -------------------------
    fun saveProfile(email: String, sex: String?, height: Int?, weight: Int?) {
        viewModelScope.launch {
            try {
                repo.updateUserProfile(
                    userId = userId,
                    email = email,
                    sex = sex,
                    height = height,
                    weight = weight
                )

                _uiState.value = _uiState.value.copy(
                    saveSuccess = true,
                    email = email,
                    sex = sex,
                    height = height,
                    weight = weight,
                    error = null
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to save profile",
                    saveSuccess = false
                )
            }
        }
    }
}
