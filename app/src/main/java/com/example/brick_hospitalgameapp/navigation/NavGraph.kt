package com.example.brick_hospitalgameapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.brick_hospitalgameapp.models.UserProfile
import com.example.brick_hospitalgameapp.screens.LoginScreen
import com.example.brick_hospitalgameapp.screens.LevelSettingsScreen
import com.example.brick_hospitalgameapp.ui.screens.*

@Composable
fun AppNavigation(
    navController: NavHostController,
    userProfile: UserProfile?
) {
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        // Login Screen
        composable("login") {
            LoginScreen(navController, userProfile)
        }

        // Level Selection
        composable(
            route = "mode_select/{userId}",
            arguments = listOf(navArgument("userId") {
                type = NavType.StringType
                nullable = true
            })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            LevelSelectionScreen(navController, userProfile, userId)
        }

        // 關卡一設定頁（顏色遊戲）
        composable(
            route = "level_settings/{levelName}/{mockUserId}",
            arguments = listOf(
                navArgument("levelName") { type = NavType.StringType },
                navArgument("mockUserId") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val levelName = backStackEntry.arguments?.getString("levelName") ?: "關卡1"
            val mockUserId = backStackEntry.arguments?.getString("mockUserId")
            LevelSettingsScreen(navController, levelName, userProfile, mockUserId)
        }

        // 關卡二設定頁（形狀遊戲）
        composable(
            route = "level_two_settings/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: "mock_user"
            LevelSettingsShapesScreen(navController, userId, userProfile)
        }

        // 關卡一顏色遊戲
        composable(
            route = "game_single_color/{levelName}/{mockUserId}/{totalTimeSeconds}",
            arguments = listOf(
                navArgument("levelName") { type = NavType.StringType },
                navArgument("mockUserId") { type = NavType.StringType; nullable = true },
                navArgument("totalTimeSeconds") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val levelName = backStackEntry.arguments?.getString("levelName") ?: "關卡1"
            val mockUserId = backStackEntry.arguments?.getString("mockUserId")
            val totalTime = backStackEntry.arguments?.getInt("totalTimeSeconds") ?: 60
            GameScreenSingleColor(navController, userProfile, mockUserId, levelName, totalTime)
        }

        // 關卡二形狀遊戲 - 單色
        composable(
            route = "game_shapes_single/{levelName}/{mockUserId}/{totalTimeSeconds}",
            arguments = listOf(
                navArgument("levelName") { type = NavType.StringType },
                navArgument("mockUserId") { type = NavType.StringType; nullable = true },
                navArgument("totalTimeSeconds") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val levelName = backStackEntry.arguments?.getString("levelName") ?: "關卡2"
            val mockUserId = backStackEntry.arguments?.getString("mockUserId")
            val totalTime = backStackEntry.arguments?.getInt("totalTimeSeconds") ?: 60
            GameScreenShapesSingle(navController, levelName, mockUserId ?: "", totalTime, userProfile)
        }

        // 關卡二形狀遊戲 - 多色
        composable(
            route = "game_shapes_multi/{levelName}/{mockUserId}/{colorMode}",
            arguments = listOf(
                navArgument("levelName") { type = NavType.StringType },
                navArgument("mockUserId") { type = NavType.StringType; nullable = true },
                navArgument("colorMode") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val levelName = backStackEntry.arguments?.getString("levelName") ?: "關卡2"
            val mockUserId = backStackEntry.arguments?.getString("mockUserId")
            val colorMode = backStackEntry.arguments?.getString("colorMode") ?: "sequence"
            val totalTime = 60
            GameScreenShapesMultiColor(navController, levelName, mockUserId, totalTime, colorMode)
        }

        // 關卡一顏色遊戲結算
        composable(
            route = "game_summary_color/{levelName}/{mockUserId}/{totalTimeSeconds}",
            arguments = listOf(
                navArgument("levelName") { type = NavType.StringType },
                navArgument("mockUserId") { type = NavType.StringType; nullable = true },
                navArgument("totalTimeSeconds") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val levelName = backStackEntry.arguments?.getString("levelName") ?: ""
            val mockUserId = backStackEntry.arguments?.getString("mockUserId")
            val totalTime = backStackEntry.arguments?.getInt("totalTimeSeconds") ?: 60
            GameSummaryShapesScreen(navController, levelName, mockUserId, totalTime)
        }

        // 關卡二形狀遊戲結算
        composable(
            route = "game_summary_shapes/{levelName}/{mockUserId}/{totalTimeSeconds}",
            arguments = listOf(
                navArgument("levelName") { type = NavType.StringType },
                navArgument("mockUserId") { type = NavType.StringType; nullable = true },
                navArgument("totalTimeSeconds") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val levelName = backStackEntry.arguments?.getString("levelName") ?: ""
            val mockUserId = backStackEntry.arguments?.getString("mockUserId")
            val totalTime = backStackEntry.arguments?.getInt("totalTimeSeconds") ?: 60
            GameSummaryShapesScreen(navController, levelName, mockUserId, totalTime)
        }
    }
}
