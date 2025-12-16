package com.example.brick_hospitalgameapp.utils

import androidx.compose.ui.graphics.Color

/**
 * Color 擴展函數 - 統一的顏色名稱獲取
 * 這個文件統一管理所有顏色相關的擴展函數，避免重複定義
 */
fun Color.getColorName(): String = when (this) {
    Color.Red -> "紅色"
    Color.Yellow -> "黃色"
    Color.Blue -> "藍色"
    Color.Green -> "綠色"
    else -> "未知"
}

/**
 * 如果需要英文版本，可以添加這個函數
 */
fun Color.getColorNameEn(): String = when (this) {
    Color.Red -> "Red"
    Color.Yellow -> "Yellow"
    Color.Blue -> "Blue"
    Color.Green -> "Green"
    else -> "Unknown"
}