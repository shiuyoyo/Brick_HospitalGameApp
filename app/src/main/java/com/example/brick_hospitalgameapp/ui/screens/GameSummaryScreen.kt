package com.example.brick_hospitalgameapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.navigation.NavController

@Composable
fun GameSummaryScreen(
    navController: NavController,
    correctCount: Int,
    wrongCount: Int,
    totalTime: Int,
    levelName: String,
    mockUserId: String
) {
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        // 背景圖片 (可自行換圖)
        Image(
            painter = painterResource(
                id = context.resources.getIdentifier(
                    "game_end_singlecolor", // 你放入 drawable 的圖片名稱
                    "drawable",
                    context.packageName
                )
            ),
            contentDescription = "背景",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 其他內容
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "花費時間: $totalTime 秒",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "正確放置: $correctCount",
                style = MaterialTheme.typography.titleLarge,
                color = Color.Black
            )
            Text(
                "錯誤放置: $wrongCount",
                style = MaterialTheme.typography.titleLarge,
                color = Color.Black
            )
            Text(
                "平均放置時間: ${if (correctCount > 0) totalTime / correctCount else 0} 秒",
                style = MaterialTheme.typography.titleLarge,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = 140.dp) // 往下移 40.dp，可自行調整
            ) {
                // 關卡選擇按鈕
                Button(
                    onClick = {
                        navController.navigate("mode_select/$mockUserId") {
                            popUpTo("game_summary/$correctCount/$wrongCount/$totalTime/$levelName/$mockUserId") { inclusive = true }
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
                            popUpTo("game_summary/$correctCount/$wrongCount/$totalTime/$levelName/$mockUserId") { inclusive = true }
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

