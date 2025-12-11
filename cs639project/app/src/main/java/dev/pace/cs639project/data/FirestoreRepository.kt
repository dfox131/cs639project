package dev.pace.cs639project.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.Timestamp
import kotlinx.coroutines.tasks.await

class FirestoreRepository {

    private val db = FirebaseFirestore.getInstance()

    // --------------------------
    // USERS
    // --------------------------

    suspend fun createUser(
        userId: String,
        email: String,
        sex: String?,
        height: Int?,
        weight: Int?
    ): Result<Unit> {
        return try {
            val userData = hashMapOf(
                "email" to email,
                "sex" to sex,
                "height" to height,
                "weight" to weight,
                "createdAt" to Timestamp.now(),
                "badges" to listOf<String>()
            )

            db.collection("users")
                .document(userId)
                .set(userData, SetOptions.merge())
                .await()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun habitsCollection(userId: String) =
        db.collection("users")
            .document(userId)
            .collection("habits")

    suspend fun getUser(userId: String): Result<Map<String, Any>?> {
        return try {
            val doc = db.collection("users").document(userId).get().await()
            Result.success(doc.data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --------------------------
    // HABITS
    // --------------------------

    suspend fun createHabit(
        userId: String,
        name: String,
        type: String,
        goal: Int?,
        reminderTime: String?
    ): Result<String> {
        return try {
            val docRef = habitsCollection(userId).document()

            val habit = hashMapOf(
                "habitId" to docRef.id,
                "name" to name,
                "type" to type,
                "goal" to goal,
                "reminderTime" to reminderTime,
                "createdAt" to Timestamp.now(),
                "updatedAt" to Timestamp.now()
            )

            docRef.set(habit).await()
            Result.success(docRef.id)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }



    suspend fun getUserHabits(userId: String): Result<List<Habit>> {
        return try {
            val snapshot = habitsCollection(userId)
                .get()
                .await()

            val habits = snapshot.documents.mapNotNull { doc ->
                val data = doc.data
                if (data != null) {
                    Habit(
                        habitId = doc.id,
                        userId = userId, // derived from path
                        name = data["name"] as? String ?: "",
                        type = data["type"] as? String ?: "",
                        goal = (data["goal"] as? Number)?.toInt(),
                        reminderTime = data["reminderTime"] as? String
                    )
                } else null
            }

            Result.success(habits)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }



    // --------------------------
    // PROGRESS (Daily completion)
    // --------------------------

    suspend fun markHabitCompleted(
        userId: String,
        habitId: String,
        date: String,        // YYYY-MM-DD
        value: Int? = null   // optional (ex: steps)
    ): Result<Unit> {
        return try {
            val progressData = hashMapOf(
                "userId" to userId,
                "habitId" to habitId,
                "date" to date,
                "completed" to true,
                "value" to value
            )

            db.collection("progress")
                .add(progressData)
                .await()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTodayProgress(
        userId: String,
        date: String
    ): Result<List<Map<String, Any>>> {
        return try {
            val snapshot = db.collection("progress")
                .whereEqualTo("userId", userId)
                .whereEqualTo("date", date)
                .get()
                .await()

            val progress = snapshot.documents.mapNotNull { it.data }
            Result.success(progress)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getHabitProgressHistory(
        userId: String,
        habitId: String
    ): Result<List<Map<String, Any>>> {
        return try {
            val snapshot = db.collection("progress")
                .whereEqualTo("userId", userId)
                .whereEqualTo("habitId", habitId)
                .get()
                .await()

            val history = snapshot.documents.mapNotNull { it.data }
            Result.success(history)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
