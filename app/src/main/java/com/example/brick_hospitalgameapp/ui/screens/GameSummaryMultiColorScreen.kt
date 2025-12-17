package com.example.brick_hospitalgameapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

// GameSummaryScreenMultiColor.kt
@Composable
fun GameSummaryScreenMultiColor(
    navController: NavController,
    totalTime: Int,
    levelName: String,
    mockUserId: String?,
    scoreMap: Map<Color, Int>,
    mistakesMap: Map<Color, Int>,
    totalTimeSeconds: Int
) {
    val context = LocalContext.current
    val colors = listOf(Color.Red, Color.Yellow, Color.Blue, Color.Green)

    // ✅ 從 savedStateHandle 取得分數/失誤 Map
    val scoreMap: Map<Color, Int> = navController
        .previousBackStackEntry
        ?.savedStateHandle
        ?.get<Map<Color, Int>>("scoreMap")
        ?: colors.associateWith { 0 }

    val mistakesMap: Map<Color, Int> = navController
        .previousBackStackEntry
        ?.savedStateHandle
        ?.get<Map<Color, Int>>("mistakesMap")
        ?: colors.associateWith { 0 }

    Box(modifier = Modifier.fillMaxSize()){
        Image(
            painter=painterResource(id=context.resources.getIdentifier("game_end_singlecolor","drawable",context.packageName)),
            contentDescription="背景",
            modifier=Modifier.fillMaxSize(),
            contentScale=ContentScale.Crop
        )

        Column(
            modifier=Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Text("花費時間: $totalTime 秒", style=MaterialTheme.typography.headlineMedium, color=Color.Black)
            Spacer(modifier=Modifier.height(16.dp))
            colors.forEach{ c ->
                Text(
                    text="${c.getColorName()} 正確: ${scoreMap[c] ?:0} / 錯誤: ${mistakesMap[c] ?:0}",
                    style=MaterialTheme.typography.titleLarge,
                    fontSize=18.sp,
                    color=Color.Black
                )
                Spacer(modifier=Modifier.height(12.dp))
            }

            Spacer(modifier=Modifier.height(32.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)){

                // 關卡選擇按鈕
                Button(
                    onClick = {
                        navController.navigate("mode_select/$mockUserId") {
                            popUpTo("game_summary_multi_color/$levelName/$mockUserId/$totalTime") { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .offset(x = 0.dp, y = 0.dp), // 若需要左右微調可改 x
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5))
                ) {
                    Text("關卡選擇", color = Color.White) // 改成黑色文字
                }

                // 重新開始按鈕
                Button(
                    onClick = {
                        navController.navigate("level_settings/$levelName/$mockUserId") {
                            popUpTo("game_summary_multi_color/$levelName/$mockUserId/$totalTime") { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .offset(x = 0.dp, y = 0.dp), // 可微調左右位置
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5))
                ) {
                    Text("重新開始", color = Color.White) // 改成黑色文字
                }
            }
        }
    }
}


