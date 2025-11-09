package com.example.brick_hospitalgameapp.utils

import android.content.Context

object LocalStorage {
    private const val PREFS_NAME = "brick_hospitalgameapp_prefs"
    private const val KEY_USER_ID = "user_id"

    fun saveUserId(context: Context, userId: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_USER_ID, userId).apply()
        println("✅ 本地存檔 userId: $userId")
    }

    fun loadUserId(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_USER_ID, null)
    }
}
