package com.example.brick_hospitalgameapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.brick_hospitalgameapp.models.UserProfile
import com.example.brick_hospitalgameapp.screens.ConnectingScreen
import com.example.brick_hospitalgameapp.screens.GameScreen
import com.example.brick_hospitalgameapp.screens.LevelSelectionScreen
import com.example.brick_hospitalgameapp.screens.LevelSettingsScreen
import com.example.brick_hospitalgameapp.screens.LoginScreen
import com.example.brick_hospitalgameapp.screens.ProfileScreen
import com.example.brick_hospitalgameapp.ui.screens.GameScreenSingleColor
import com.example.brick_hospitalgameapp.ui.screens.GameSummaryScreen
import com.example.brick_hospitalgameapp.ui.screens.GameScreenMultiColor

@Composable
fun AppNavGraph(navController: NavHostController, userProfile: UserProfile?) {
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(navController, userProfile)
        }

        composable("mode_select/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            LevelSelectionScreen(navController, userProfile, userId)
        }

        composable("level_settings/{levelName}/{userId}") { backStackEntry ->
            val levelName = backStackEntry.arguments?.getString("levelName") ?: "關卡1"
            val userId = backStackEntry.arguments?.getString("userId")
            LevelSettingsScreen(navController, levelName, userProfile, userId)
        }

        composable("connecting_screen/{levelName}/{userId}") { backStackEntry ->
            val levelName = backStackEntry.arguments?.getString("levelName") ?: "關卡1"
            val userId = backStackEntry.arguments?.getString("userId")
            ConnectingScreen(navController, levelName, userProfile, userId)
        }

        composable("game/{levelName}/{userId}") { backStackEntry ->
            val levelName = backStackEntry.arguments?.getString("levelName") ?: "關卡1"
            val userId = backStackEntry.arguments?.getString("userId")
            GameScreen(navController, levelName, userProfile, userId)
        }

        composable("game_single_color/{levelName}/{userId}") { backStackEntry ->
            val levelName = backStackEntry.arguments?.getString("levelName") ?: "關卡1"
            val userId = backStackEntry.arguments?.getString("userId")
            GameScreenSingleColor(
                navController = navController,
                userProfile = userProfile,
                mockUserId = userId
            )
        }

        composable(
            "game_multi_color/{levelName}/{userId}/{colorMode}",
            arguments = listOf(
                navArgument("levelName") { type = NavType.StringType },
                navArgument("userId") { type = NavType.StringType; nullable = true },
                navArgument("colorMode") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val levelName = backStackEntry.arguments?.getString("levelName") ?: "關卡1"
            val userId = backStackEntry.arguments?.getString("userId")
            val colorMode = backStackEntry.arguments?.getString("colorMode") ?: "固定顏色"

            GameScreenMultiColor(
                navController = navController,
                userProfile = userProfile,
                mockUserId = userId,
                levelName = levelName,
                colorMode = colorMode
            )
        }


        composable(
            route = "game_summary/{correct}/{wrong}/{totalTime}/{levelName}/{userId}",
            arguments = listOf(
                navArgument("correct") { type = NavType.IntType },
                navArgument("wrong") { type = NavType.IntType },
                navArgument("totalTime") { type = NavType.IntType },
                navArgument("levelName") { type = NavType.StringType },
                navArgument("userId") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val correct = backStackEntry.arguments?.getInt("correct") ?: 0
            val wrong = backStackEntry.arguments?.getInt("wrong") ?: 0
            val totalTime = backStackEntry.arguments?.getInt("totalTime") ?: 0
            val levelName = backStackEntry.arguments?.getString("levelName") ?: "關卡1"
            val mockUserId = backStackEntry.arguments?.getString("userId") ?: "guest"

            GameSummaryScreen(
                navController = navController,
                correctCount = correct,
                wrongCount = wrong,
                totalTime = totalTime,
                levelName = levelName,
                mockUserId = mockUserId
            )
        }


        composable("profile_screen") {
            ProfileScreen(navController, userProfile)
        }
    }
}
