package com.example.brick_hospitalgameapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.brick_hospitalgameapp.models.UserProfile
import com.example.brick_hospitalgameapp.screens.*
import com.example.brick_hospitalgameapp.ui.screens.*
@Composable
fun AppNavGraph(navController: NavHostController, userProfile: UserProfile?) {
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {

        // Login
        composable("login") {
            LoginScreen(navController, userProfile)
        }

        // 關卡選擇頁
        composable(
            route = "mode_select/{mockUserId}",
            arguments = listOf(
                navArgument("mockUserId") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val mockUserId = backStackEntry.arguments?.getString("mockUserId")
            LevelSelectionScreen(
                navController = navController,
                userProfile = userProfile,
                mockUserId = mockUserId
            )
        }

        // 關卡一設定頁
        composable(
            route = "level_settings/{levelName}/{mockUserId}",
            arguments = listOf(
                navArgument("levelName") { type = NavType.StringType },
                navArgument("mockUserId") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val levelName = backStackEntry.arguments?.getString("levelName") ?: "關卡1"
            val mockUserId = backStackEntry.arguments?.getString("mockUserId")
            LevelSettingsScreen(
                navController = navController,
                levelName = levelName,
                userProfile = userProfile,
                mockUserId = mockUserId
            )
        }

        // 關卡二設定頁
        composable(
            route = "level_settings_shapes/{levelName}/{mockUserId}",
            arguments = listOf(
                navArgument("levelName") { type = NavType.StringType },
                navArgument("mockUserId") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val levelName = backStackEntry.arguments?.getString("levelName") ?: "關卡2"
            val mockUserId = backStackEntry.arguments?.getString("mockUserId")
            LevelSettingsShapesScreen(
                navController = navController,
                levelName = levelName,
                mockUserId = mockUserId,
                userProfile = userProfile
            )
        }

        // 關卡一遊戲頁 (單色)
        composable(
            route = "game_single_color/{levelName}/{mockUserId}/{practiceMinutes}/{intervalSeconds}",
            arguments = listOf(
                navArgument("levelName") { type = NavType.StringType },
                navArgument("mockUserId") { type = NavType.StringType; nullable = true },
                navArgument("practiceMinutes") { type = NavType.IntType },
                navArgument("intervalSeconds") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val levelName = backStackEntry.arguments?.getString("levelName") ?: "關卡1"
            val mockUserId = backStackEntry.arguments?.getString("mockUserId")
            val practiceMinutes = backStackEntry.arguments?.getInt("practiceMinutes") ?: 20
            val intervalSeconds = backStackEntry.arguments?.getInt("intervalSeconds") ?: 20

            GameScreenSingleColor(
                navController = navController,
                userProfile = userProfile,
                mockUserId = mockUserId,
                levelName = levelName,
                practiceMinutes = practiceMinutes,
                intervalSeconds = intervalSeconds
            )
        }

// 關卡一遊戲頁 (多色)
        composable(
            route = "game_multi_color/{levelName}/{mockUserId}/{colorMode}/{practiceMinutes}/{intervalSeconds}",
            arguments = listOf(
                navArgument("levelName") { type = NavType.StringType },
                navArgument("mockUserId") { type = NavType.StringType; nullable = true },
                navArgument("colorMode") { type = NavType.StringType },
                navArgument("practiceMinutes") { type = NavType.IntType },
                navArgument("intervalSeconds") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val levelName = backStackEntry.arguments?.getString("levelName") ?: "關卡1"
            val mockUserId = backStackEntry.arguments?.getString("mockUserId")
            val colorMode = backStackEntry.arguments?.getString("colorMode") ?: "sequence"
            val practiceMinutes = backStackEntry.arguments?.getInt("practiceMinutes") ?: 20
            val intervalSeconds = backStackEntry.arguments?.getInt("intervalSeconds") ?: 20

            GameScreenMultiColor(
                navController = navController,
                userProfile = userProfile,
                mockUserId = mockUserId,
                levelName = levelName,
                colorMode = colorMode,
                practiceMinutes = practiceMinutes,
                intervalSeconds = intervalSeconds
            )
        }


        // 關卡一結算頁
        composable(
            route = "game_summary/{correct}/{wrong}/{totalTime}/{levelName}/{mockUserId}",
            arguments = listOf(
                navArgument("correct") { type = NavType.IntType },
                navArgument("wrong") { type = NavType.IntType },
                navArgument("totalTime") { type = NavType.IntType },
                navArgument("levelName") { type = NavType.StringType },
                navArgument("mockUserId") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val correct = backStackEntry.arguments?.getInt("correct") ?: 0
            val wrong = backStackEntry.arguments?.getInt("wrong") ?: 0
            val totalTime = backStackEntry.arguments?.getInt("totalTime") ?: 60
            val levelName = backStackEntry.arguments?.getString("levelName") ?: "關卡1"
            val mockUserId = backStackEntry.arguments?.getString("mockUserId") ?: "mock_user"

            GameSummaryScreen(
                navController = navController,
                correctCount = correct,
                wrongCount = wrong,
                totalTime = totalTime,
                levelName = levelName,
                mockUserId = mockUserId
            )
        }


        // 關卡一結算頁
        composable(
            route = "game_summary_multi_color/{levelName}/{mockUserId}/{totalTimeSeconds}",
            arguments = listOf(
                navArgument("levelName") { type = NavType.StringType },
                navArgument("mockUserId") { type = NavType.StringType; nullable = true },
                navArgument("totalTimeSeconds") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val levelName = backStackEntry.arguments?.getString("levelName") ?: "關卡1"
            val mockUserId = backStackEntry.arguments?.getString("mockUserId")
            val totalTime = backStackEntry.arguments?.getInt("totalTimeSeconds") ?: 60

            val scoreMap =
                navController.previousBackStackEntry?.savedStateHandle?.get<Map<Color, Int>>("scoreMap") ?: emptyMap()
            val mistakesMap =
                navController.previousBackStackEntry?.savedStateHandle?.get<Map<Color, Int>>("mistakesMap") ?: emptyMap()

            GameSummaryScreenMultiColor(
                navController = navController,
                totalTime = totalTime,
                levelName = levelName,
                mockUserId = mockUserId,
                scoreMap = scoreMap,
                mistakesMap = mistakesMap,
                totalTimeSeconds = totalTime
            )
        }

        // 關卡二單色遊戲
        composable(
            route = "game_shapes_single/{levelName}/{mockUserId}/{practiceMinutes}/{intervalSeconds}",
            arguments = listOf(
                navArgument("levelName") { type = NavType.StringType },
                navArgument("mockUserId") { type = NavType.StringType; nullable = true },
                navArgument("practiceMinutes") { type = NavType.IntType },
                navArgument("intervalSeconds") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val levelName = backStackEntry.arguments?.getString("levelName") ?: "關卡2"
            val mockUserId = backStackEntry.arguments?.getString("mockUserId")
            val practiceMinutes = backStackEntry.arguments?.getInt("practiceMinutes") ?: 20
            val intervalSeconds = backStackEntry.arguments?.getInt("intervalSeconds") ?: 20

            GameScreenShapesSingle(
                navController = navController,
                userProfile = userProfile,
                mockUserId = mockUserId,
                levelName = levelName,
                practiceMinutes = practiceMinutes,
                intervalSeconds = intervalSeconds
            )
        }

// 關卡二多色遊戲
        composable(
            route = "game_shapes_multi/{levelName}/{mockUserId}/{colorMode}/{practiceMinutes}/{intervalSeconds}",
            arguments = listOf(
                navArgument("levelName") { type = NavType.StringType },
                navArgument("mockUserId") { type = NavType.StringType; nullable = true },
                navArgument("colorMode") { type = NavType.StringType },
                navArgument("practiceMinutes") { type = NavType.IntType },
                navArgument("intervalSeconds") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val levelName = backStackEntry.arguments?.getString("levelName") ?: "關卡2"
            val mockUserId = backStackEntry.arguments?.getString("mockUserId")
            val colorMode = backStackEntry.arguments?.getString("colorMode") ?: "sequence"
            val practiceMinutes = backStackEntry.arguments?.getInt("practiceMinutes") ?: 20
            val intervalSeconds = backStackEntry.arguments?.getInt("intervalSeconds") ?: 20

            GameScreenShapesMultiColor(
                navController = navController,
                userProfile = userProfile,
                mockUserId = mockUserId,
                levelName = levelName,
                colorMode = colorMode,
                practiceMinutes = practiceMinutes,
                intervalSeconds = intervalSeconds
            )
        }


        // 關卡二結算頁
        composable(
            route = "game_summary_shapes/{levelName}/{mockUserId}/{totalTimeSeconds}",
            arguments = listOf(
                navArgument("levelName") { type = NavType.StringType },
                navArgument("mockUserId") { type = NavType.StringType; nullable = true },
                navArgument("totalTimeSeconds") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val levelName = backStackEntry.arguments?.getString("levelName") ?: "關卡2"
            val mockUserId = backStackEntry.arguments?.getString("mockUserId")
            val totalTime = backStackEntry.arguments?.getInt("totalTimeSeconds") ?: 60

            val scoreMap =
                navController.previousBackStackEntry?.savedStateHandle?.get<Map<Color, Int>>("scoreMap") ?: emptyMap()
            val mistakesMap =
                navController.previousBackStackEntry?.savedStateHandle?.get<Map<Color, Int>>("mistakesMap") ?: emptyMap()

            GameSummaryShapesScreen(
                navController = navController,
                totalTime = totalTime,
                levelName = levelName,
                mockUserId = mockUserId,
                scoreMap = scoreMap,
                mistakesMap = mistakesMap,
                totalTimeSeconds = totalTime
            )
        }

        // 關卡3 - 設定頁
        composable(
            route = "level_setting_thin/{mockUserId}",
            arguments = listOf(
                navArgument("mockUserId") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val mockUserId = backStackEntry.arguments?.getString("mockUserId")
            LevelSettingsThinScreen(
                navController = navController,
                userProfile = userProfile,
                mockUserId = mockUserId
            )
        }

// 關卡3 - 遊戲頁（注意：practiceMinutes / intervalSeconds）
        composable(
            route = "game_thin_circle/{levelName}/{mockUserId}/{practiceMinutes}/{intervalSeconds}",
            arguments = listOf(
                navArgument("levelName") { type = NavType.StringType },
                navArgument("mockUserId") { type = NavType.StringType; nullable = true },
                navArgument("practiceMinutes") { type = NavType.IntType },
                navArgument("intervalSeconds") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val levelName = backStackEntry.arguments?.getString("levelName") ?: "關卡3"
            val mockUserId = backStackEntry.arguments?.getString("mockUserId")
            val practiceMinutes = backStackEntry.arguments?.getInt("practiceMinutes") ?: 20
            val intervalSeconds = backStackEntry.arguments?.getInt("intervalSeconds") ?: 20

            val scoreMap =
                navController.previousBackStackEntry?.savedStateHandle?.get<Map<Color, Int>>("scoreMap") ?: emptyMap()
            val mistakesMap =
                navController.previousBackStackEntry?.savedStateHandle?.get<Map<Color, Int>>("mistakesMap") ?: emptyMap()

            GameScreenThinCircle(
                navController = navController,
                levelName = levelName,
                mockUserId = mockUserId,
                practiceMinutes = practiceMinutes,
                intervalSeconds = intervalSeconds
            )
        }


        composable(
            route = "game_multi_color_mobile_microbit/{levelName}/{mockUserId}/{colorMode}/{practiceMinutes}/{intervalSeconds}",
            arguments = listOf(
                navArgument("levelName") { type = NavType.StringType },
                navArgument("mockUserId") { type = NavType.StringType; nullable = true },
                navArgument("colorMode") { type = NavType.StringType },
                navArgument("practiceMinutes") { type = NavType.IntType },
                navArgument("intervalSeconds") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val levelName = backStackEntry.arguments?.getString("levelName") ?: "顏色遊戲"
            val mockUserId = backStackEntry.arguments?.getString("mockUserId")
            val colorMode = backStackEntry.arguments?.getString("colorMode") ?: "sequence"
            val practiceMinutes = backStackEntry.arguments?.getInt("practiceMinutes") ?: 20
            val intervalSeconds = backStackEntry.arguments?.getInt("intervalSeconds") ?: 20

            GameScreenMultiColorMobileMicrobit(
                navController = navController,
                userProfile = userProfile,
                mockUserId = mockUserId,
                levelName = levelName,
                colorMode = colorMode,
                practiceMinutes = practiceMinutes,
                intervalSeconds = intervalSeconds
            )
        }

    }
}
