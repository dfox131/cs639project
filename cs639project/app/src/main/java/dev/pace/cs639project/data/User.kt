package dev.pace.cs639project.data

import com.google.firebase.Timestamp

data class User(
    val email: String = "",
    val sex: String? = null,
    val height: Int? = null,
    val weight: Int? = null,
    val createdAt: Timestamp? = null,   // ⬅️ changed from Long? to Timestamp?
    val badges: List<String> = emptyList()
)
