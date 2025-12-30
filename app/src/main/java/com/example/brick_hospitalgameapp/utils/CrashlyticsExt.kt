package com.example.brick_hospitalgameapp.utils

import android.os.Build
import com.google.firebase.crashlytics.FirebaseCrashlytics

fun logScreenEnter(
    screen: String,
    uid: String?,
    levelName: String?,
    extras: Map<String, Any?> = emptyMap()
) {
    val c = FirebaseCrashlytics.getInstance()

    if (!uid.isNullOrBlank()) c.setUserId(uid)

    c.setCustomKey("screen", screen)
    c.setCustomKey("levelName", levelName ?: "null")
    c.setCustomKey("uid", uid ?: "null")
    c.setCustomKey("device_model", Build.MODEL ?: "null")
    c.setCustomKey("device_sdk", Build.VERSION.SDK_INT)

    extras.forEach { (k, v) ->
        when (v) {
            is Int -> c.setCustomKey(k, v)
            is Long -> c.setCustomKey(k, v)
            is Boolean -> c.setCustomKey(k, v)
            is Double -> c.setCustomKey(k, v)
            is Float -> c.setCustomKey(k, v)
            else -> c.setCustomKey(k, v?.toString() ?: "null")
        }
    }

    c.log("ENTER $screen level=$levelName uid=$uid extras=$extras")
}
