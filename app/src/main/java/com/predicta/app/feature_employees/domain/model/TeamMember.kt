package com.predicta.app.feature_employees.domain.model

data class TeamMember(
    val id: String,
    val name: String,
    val role: String,
    val doneCount: Int,
    val totalCount: Int,
    val health: Health,
)

enum class Health {
    GOOD, NORMAL, BAD;

    companion object {
        fun fromString(value: String): Health = when (value.lowercase()) {
            "good" -> GOOD
            "normal" -> NORMAL
            "bad" -> BAD
            else -> NORMAL
        }
    }
}
