package dev.pace.cs639project.data

data class Habit(
    val habitId: String = "",
    val userId: String = "",
    val name: String = "",
    val type: String = "custom",
    val goal: Int? = null,
    val reminderTime: String? = null
) {
    companion object {
        fun fromMap(map: Map<String, Any>, habitId: String): Habit {
            return Habit(
                habitId = habitId,
                userId = map["userId"] as? String ?: "",
                name = map["name"] as? String ?: "",
                type = map["type"] as? String ?: "custom",
                goal = (map["goal"] as? Number)?.toInt(),
                reminderTime = map["reminderTime"] as? String
            )
        }
    }
}
