package dev.pace.cs639project


data class ExerciseSuggestion(
    val name: String,
    val description: String,
    val icon: String
)

// faka data
val fakeSuggestions = listOf(
    ExerciseSuggestion(
        name = "Jumping Jacks",
        description = "Quick full-body jumps to warm up and boost your heart rate.",
        icon = "🧘"
    ),
    ExerciseSuggestion(
        name = "High Knees",
        description = "Run in place, lifting your knees up to hip height to build endurance.",
        icon = "🏃"
    ),
    ExerciseSuggestion(
        name = "Lunges",
        description = "Step forward into controlled lunges to strengthen legs and glutes.",
        icon = "💪"
    )
)
