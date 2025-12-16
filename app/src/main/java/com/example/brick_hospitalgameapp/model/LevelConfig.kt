package com.example.brick_hospitalgameapp.model

enum class Level(val id: Int) { L1(1), L2(2), L3(3) }

data class LevelConfig(
    val level: Level,
    val timeLimitSec: Int,
    val targetCount: Int,
    val title: String
)

fun defaultConfig(level: Level) = when (level) {
    Level.L1 -> LevelConfig(level, timeLimitSec = 60, targetCount = 10, title = "關卡 1")
    Level.L2 -> LevelConfig(level, timeLimitSec = 60, targetCount = 10, title = "關卡 2")
    Level.L3 -> LevelConfig(level, timeLimitSec = 90, targetCount = 15, title = "關卡 3")
}
