package com.example.brick_hospitalgameapp.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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

@SuppressLint("DiscouragedApi")
@Composable
fun GameSummaryScreenMultiColor(
    navController: NavController,
    totalTime: Int,
    scoreMap: Map<Color, Int>,
    mistakesMap: Map<Color, Int>,
    levelName: String,
    mockUserId: String
) {
    val context = LocalContext.current

    // 四種顏色
    val colors = listOf(Color.Red, Color.Yellow, Color.Blue, Color.Green)
    val colorNames = listOf("紅色", "黃色", "藍色", "綠色")

    Box(modifier = Modifier.fillMaxSize()) {
        // 背景圖片，可自行更換
        Image(
            painter = painterResource(
                id = context.resources.getIdentifier(
                    "game_end_multi", // 背景圖片名稱
                    "drawable",
                    context.packageName
                )
            ),
            contentDescription = "背景",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                "花費時間: $totalTime 秒",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))

            // 每個顏色的正確/錯誤
            colors.forEachIndexed { index, color ->
                val correct = scoreMap[color] ?: 0
                val wrong = mistakesMap[color] ?: 0
                val avgTime = if (correct > 0) totalTime / correct else 0

                Text(
                    "${colorNames[index]} → 正確: $correct, 錯誤: $wrong, 平均放置: $avgTime 秒",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                // 關卡選擇按鈕（可換圖片）
                Image(
                    painter = painterResource(
                        id = context.resources.getIdentifier(
                            "btn_mode_select",
                            "drawable",
                            context.packageName
                        )
                    ),
                    contentDescription = "關卡選擇",
                    modifier = Modifier
                        .size(120.dp)
                        .clickable {
                            navController.navigate("mode_select/$mockUserId") {
                                popUpTo("game_single_color/$levelName/$mockUserId") { inclusive = true }
                            }
                        }
                )

                // 重新開始按鈕（可換圖片）
                Image(
                    painter = painterResource(
                        id = context.resources.getIdentifier(
                            "btn_restart",
                            "drawable",
                            context.packageName
                        )
                    ),
                    contentDescription = "重新開始",
                    modifier = Modifier
                        .size(120.dp)
                        .clickable {
                            navController.navigate("level_settings/$levelName/$mockUserId") {
                                popUpTo("game_single_color/$levelName/$mockUserId") { inclusive = true }
                            }
                        }
                )
            }
        }
    }
}
