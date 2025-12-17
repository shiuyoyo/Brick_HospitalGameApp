package com.example.brick_hospitalgameapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import com.example.brick_hospitalgameapp.models.UserProfile
import com.example.brick_hospitalgameapp.ui.theme.Brick_hospitalgameappTheme
import com.example.brick_hospitalgameapp.navigation.AppNavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Brick_hospitalgameappTheme {
                val navController = rememberNavController()

                // 模擬使用者資料，可直接登入遊玩
                var mockUserProfile: UserProfile? by remember {
                    mutableStateOf(null)
                }

                AppNavGraph(navController = navController, userProfile = mockUserProfile)
            }
        }
    }
}