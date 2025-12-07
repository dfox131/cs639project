package dev.pace.cs639project.data

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    suspend fun signInAnonymously(): Result<String> {
        return try {
            val result = auth.signInAnonymously().await()
            val uid = result.user?.uid ?: throw Exception("User ID missing")
            Result.success(uid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
