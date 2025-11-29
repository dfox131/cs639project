package dev.pace.cs639project.data

data class Progress(
    val userId: String = "",
    val habitId: String = "",
    val date: String = "",  // YYYY-MM-DD
    val completed: Boolean = false,
    val value: Int? = null  // steps, reps, etc.
)
