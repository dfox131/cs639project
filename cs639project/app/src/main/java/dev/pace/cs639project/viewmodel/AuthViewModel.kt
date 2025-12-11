package dev.pace.cs639project.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dev.pace.cs639project.data.FirestoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _currentUserId = MutableStateFlow<String?>(auth.currentUser?.uid)
    val currentUserId: StateFlow<String?> = _currentUserId

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess

    private val _signupSuccess = MutableStateFlow(false)
    val signupSuccess: StateFlow<Boolean> = _signupSuccess


    /** ---------------------- SIGNUP ----------------------- **/

    fun signup(
        email: String,
        password: String,
        sex: String?,
        height: Int?,
        weight: Int?,
        onSuccess: () -> Unit
    ) {
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                auth.createUserWithEmailAndPassword(email, password).await()

                val uid = auth.currentUser?.uid ?: throw Exception("Signup failed")

                FirestoreRepository().createUser(
                    userId = uid,
                    email = email,
                    sex = sex,
                    height = height,
                    weight = weight
                )

                _currentUserId.value = uid
                _signupSuccess.value = true
                _isLoading.value = false

                onSuccess()

            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }




    /** ---------------------- LOGIN ----------------------- **/

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password).await()

                _currentUserId.value = auth.currentUser?.uid
                _loginSuccess.value = true
                _isLoading.value = false

                onSuccess() // 🔥 tell UI to navigate

            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }



    /** ---------------------- LOGOUT ----------------------- **/

    fun logout() {
        auth.signOut()
        _currentUserId.value = null
    }


    /** ---------------------- UI Helpers ----------------------- **/

    fun clearError() {
        _error.value = null
    }

    fun resetFlags() {
        _loginSuccess.value = false
        _signupSuccess.value = false
    }
}
