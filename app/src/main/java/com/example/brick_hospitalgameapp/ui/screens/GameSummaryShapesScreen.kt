package com.example.brick_hospitalgameapp.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.brick_hospitalgameapp.utils.getColorName

@SuppressLint("DiscouragedApi")
@Composable
fun GameSummaryShapesScreen(
    navController: NavController,
    levelName: String,
    mockUserId: String?,
    totalTimeSeconds: Int,
    totalTime: Int,
    scoreMap: Map<Color, Int>,
    mistakesMap: Map<Color, Int>
) {
    val context = LocalContext.current

    // 從 savedStateHandle 獲取分數和錯誤數據
    val scoreMap = navController.previousBackStackEntry?.savedStateHandle?.get<Map<Color, Int>>("scoreMap") ?: emptyMap()
    val mistakesMap = navController.previousBackStackEntry?.savedStateHandle?.get<Map<Color, Int>>("mistakesMap") ?: emptyMap()

    Box(modifier = Modifier.fillMaxSize()) {
        // 背景圖片
        Image(
            painter = painterResource(
                id = context.resources.getIdentifier("game_end_muti", "drawable", context.packageName)
            ),
            contentDescription = "背景",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 標題
            Text(
                text = "遊戲結果",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(30.dp))

            // 關卡信息
            Text(
                text = levelName,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "遊戲時間: ${totalTimeSeconds}秒",
                fontSize = 18.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(40.dp))

            // 分數統計卡片
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 110.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "遊戲統計",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // 顯示各顏色的分數和錯誤
                    if (scoreMap.isNotEmpty() && mistakesMap.isNotEmpty()) {
                        scoreMap.forEach { (color, score) ->
                            val mistakes = mistakesMap[color] ?: 0
                            val accuracy = if (score + mistakes > 0) {
                                (score.toFloat() / (score + mistakes) * 100).toInt()
                            } else 0

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // 顏色指示器
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .background(color)
                                )

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${color.getColorName()}",
                                        color = Color.Black,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = "正確: $score, 錯誤: $mistakes",
                                        color = Color.Gray,
                                        fontSize = 14.sp
                                    )
                                }

                                Text(
                                    text = "${accuracy}%",
                                    color = if (accuracy >= 80) Color.Green else if (accuracy >= 60) Color.Yellow else Color.Red,
                                    fontSize = 18.sp
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "無遊戲數據",
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // 按鈕區域
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // 關卡選擇按鈕
                Button(
                    onClick = {
                        navController.navigate("mode_select/$mockUserId") {
                            popUpTo("game_summary_shapes/$levelName/$mockUserId/$totalTimeSeconds") { inclusive = true }
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
                        navController.navigate("level_settings_shapes/$levelName/$mockUserId") {
                            popUpTo("game_summary_shapes/$levelName/$mockUserId/$totalTimeSeconds") { inclusive = true }
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