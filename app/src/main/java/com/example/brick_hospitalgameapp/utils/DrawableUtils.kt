package com.example.brick_hospitalgameapp.ui.utils

import android.content.Context
import androidx.annotation.DrawableRes

@DrawableRes
fun drawableIdByName(context: Context, name: String): Int {
    return context.resources.getIdentifier(name, "drawable", context.packageName)
}
