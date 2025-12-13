package dev.pace.cs639project.data

data class Progress(
    val userId: String = "",
    val habitId: String = "",
    val date: String = "",
    val completed: Boolean = false,
    val value: Int? = null
)
