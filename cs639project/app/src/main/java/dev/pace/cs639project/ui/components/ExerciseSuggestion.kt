package dev.pace.cs639project.ui.components

import androidx.compose.runtime.Composable

data class ExerciseSuggestion(
    val name: String,
    val description: String,
    val icon: String
)

val exerciseSuggestions = listOf(
    ExerciseSuggestion(
        name = "Bodyweight Squats",
        description = "A powerful lower-body exercise to strengthen quads, glutes, and core. Focus on pushing hips back as if sitting in a chair.",
        icon = "🦵"
    ),
    ExerciseSuggestion(
        name = "Plank",
        description = "The ultimate core builder. Hold a straight line from head to heels while engaging your abs to improve stability and posture.",
        icon = "🧘"
    ),
    ExerciseSuggestion(
        name = "Push-Ups (Knee)",
        description = "A foundational upper-body move for chest, shoulders, and triceps. Start on your knees to build strength and perfect your form.",
        icon = "💪"
    ),
    ExerciseSuggestion(
        name = "Glute Bridge",
        description = "A great low-impact exercise to activate the glutes, hamstrings, and lower back, improving posture and hip stability.",
        icon = "🍑"
    ),
    ExerciseSuggestion(
        name = "Walking/Marching",
        description = "The simplest low-impact cardio to raise your heart rate, boost cardiovascular health, and reduce stress. March in place or walk outdoors.",
        icon = "🏃"
    )
)