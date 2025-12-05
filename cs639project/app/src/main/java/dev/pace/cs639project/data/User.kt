package dev.pace.cs639project.data

data class User(
    val email: String = "",
    val sex: String? = null,
    val height: Int? = null,
    val weight: Int? = null,
    val createdAt: Long? = null,
    val badges: List<String> = emptyList()
)
