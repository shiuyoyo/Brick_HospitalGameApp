package com.example.brick_hospitalgameapp.models

import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class GameRecord(
    val id: String,
    val user_id: String,
    val level: Int,
    val score: Int,
    val play_time_seconds: Int,
    val completed_at: String? = null // ISO datetime
)