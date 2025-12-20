package com.example.brick_hospitalgameapp.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import androidx.compose.ui.platform.LocalContext

@SuppressLint("DiscouragedApi")
@Composable
fun GameScreenThinCircle(
    navController: NavController,
    levelName: String,
    mockUserId: String?,
    totalTimeSeconds: Int = 60,
    scoreMap: Map<Color, Int>,
    mistakesMap: Map<Color, Int>,
) {
    val color = Color.Red
    val totalCircles = 30
    var scoreMap by remember { mutableStateOf(mutableMapOf(color to 0)) }
    var mistakesMap by remember { mutableStateOf(mutableMapOf(color to 0)) }
    var currentIndex by remember { mutableStateOf(0) }
    var elapsedTime by remember { mutableStateOf(0) }
    var gameEnded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val intervalMs = totalTimeSeconds * 1000 / totalCircles

    // 自動下一格邏輯
    LaunchedEffect(currentIndex) {
        if (currentIndex < totalCircles && !gameEnded) {
            delay(intervalMs.toLong())
            mistakesMap[color] = (mistakesMap[color] ?: 0) + 1
            currentIndex += 1
        }
    }

    // 遊戲結束監聽
    LaunchedEffect(currentIndex, gameEnded) {
        if (currentIndex >= totalCircles || gameEnded) {
            gameEnded = true
            navController.currentBackStackEntry?.savedStateHandle?.set("scoreMap", scoreMap)
            navController.currentBackStackEntry?.savedStateHandle?.set("mistakesMap", mistakesMap)
            navController.navigate("game_summary_shapes/$levelName/$mockUserId/$totalTimeSeconds") {
                popUpTo("game_thin_circle") { inclusive = true }
            }
        }
    }

    // 計時器邏輯
    LaunchedEffect(Unit) {
        while (!gameEnded) {
            delay(1000)
            elapsedTime += 1
        }
    }

    // --- 畫面佈局 ---
    // 外層使用 Box 方便你之後疊加背景圖片
    Box(modifier = Modifier.fillMaxSize()) {

        // 背景
        Image(
            painter = painterResource(
                id = context.resources.getIdentifier("game_bg3", "drawable", context.packageName)
            ),
            contentDescription = "背景",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Row(modifier = Modifier.fillMaxSize()) {

            // 【左側區域】：簡單網格垂直置中
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(50.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    for (row in 0 until 2) {
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            for (col in 0 until 15) {
                                val index = row * 15 + col
                                val isActive = index == currentIndex

                                Box(
                                    modifier = Modifier
                                        .size(50.dp) // 稍微加大一點比較好按
                                        .background(
                                            if (isActive) color else Color.LightGray.copy(alpha = 0.5f),
                                            CircleShape
                                        )
                                        .clickable(enabled = !gameEnded) {
                                            if (isActive) {
                                                scoreMap[color] = (scoreMap[color] ?: 0) + 1
                                                currentIndex += 1
                                            } else {
                                                mistakesMap[color] = (mistakesMap[color] ?: 0) + 1
                                            }
                                        }
                                )
                            }
                        }
                    }
                }
            }

            // 【右側區域】：資訊欄位 (對應圖片樣式)
            Column(
                modifier = Modifier
                    .width(150.dp)
                    .fillMaxHeight()
                    .padding(vertical = 40.dp, horizontal = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(25.dp)
            ) {
                // 分數顯示 (正確數)
                GameInfoItem(label = "SCORE", value = "${scoreMap[color]}")

                // 時間顯示
                GameInfoItem(label = "TIME", value = String.format("%02d:%02d", elapsedTime / 60, elapsedTime % 60))

                // 錯誤數顯示 (額外增加)
                GameInfoItem(label = "MISTAKES", value = "${mistakesMap[color]}")

                Spacer(modifier = Modifier.weight(1f))

                // 重新開始按鈕 (綠色)
                IconButton(
                    onClick = {
                        // 重新開始：導向自己或重置 state
                        navController.navigate("level_setting_thin/$mockUserId") {
                            popUpTo("game_thin_circle") { inclusive = true }
                        }
                    },
                    modifier = Modifier.size(65.dp).background(Color(0xFF4CAF50), CircleShape)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Restart", tint = Color.White, modifier = Modifier.size(35.dp))
                }

                // 返回鍵/停止按鈕 (紅色)
                IconButton(
                    onClick = {
                        navController.navigate("game_summary_shapes/$levelName/$mockUserId/$totalTimeSeconds") {
                        popUpTo("game_thin_circle") { inclusive = true }
                    } },
                    modifier = Modifier.size(65.dp).background(Color(0xFFF44336), CircleShape)
                ) {
                    // 使用 Stop 或類似圖示
                    Box(modifier = Modifier.size(25.dp).background(Color.White))
                }
            }
        }
    }
}

@Composable
fun GameInfoItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        Surface(
            modifier = Modifier.fillMaxWidth().height(55.dp),
            shape = RoundedCornerShape(30.dp),
            color = Color.White.copy(alpha = 0.8f),
            shadowElevation = 2.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Black, color = Color.DarkGray)
            }
        }
    }
}
