package com.example.brick_hospitalgameapp.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.brick_hospitalgameapp.ui.utils.drawableIdByName
import kotlinx.coroutines.delay
import kotlin.math.max

@SuppressLint("DiscouragedApi")
@Composable
fun GameScreenThinCircle(
    navController: NavController,
    levelName: String,
    mockUserId: String?,
    practiceMinutes: Int = 20,
    intervalSeconds: Int = 20,
) {
    val context = LocalContext.current
    val uid = mockUserId ?: "guest"

    val color = Color.Red
    val totalCircles = 30

    var scoreMap by remember { mutableStateOf(mutableMapOf(color to 0)) }
    var mistakesMap by remember { mutableStateOf(mutableMapOf(color to 0)) }

    var currentIndex by remember { mutableStateOf(0) }
    var elapsedTime by remember { mutableStateOf(0) }
    var gameEnded by remember { mutableStateOf(false) }

    val totalTimeSeconds = max(1, practiceMinutes * 60)
    val intervalMs = max(1, intervalSeconds) * 1000L

    val bgId = remember { drawableIdByName(context, "game_bg3") }

    fun finishAndGoSummary() {
        if (gameEnded) return
        gameEnded = true

        // 存到上一頁 entry，避免 pop 掉後 summary 取不到
        val target = navController.previousBackStackEntry ?: navController.currentBackStackEntry
        target?.savedStateHandle?.set("scoreMap", HashMap(scoreMap))
        target?.savedStateHandle?.set("mistakesMap", HashMap(mistakesMap))

        // 傳「實際花費時間」elapsedTime
        navController.navigate("game_summary_shapes/$levelName/$uid/$elapsedTime") {
            popUpTo("game_thin_circle/$levelName/$uid/$practiceMinutes/$intervalSeconds") { inclusive = true }
        }
    }

    // 總時間計時（到時結束）
    LaunchedEffect(Unit) {
        while (!gameEnded) {
            delay(1000)
            elapsedTime += 1
            if (elapsedTime >= totalTimeSeconds) {
                finishAndGoSummary()
            }
        }
    }

    // 自動跳格（沒點到算錯）
    LaunchedEffect(currentIndex) {
        if (!gameEnded) {
            delay(intervalMs)
            mistakesMap[color] = (mistakesMap[color] ?: 0) + 1
            currentIndex += 1
            if (currentIndex >= totalCircles) currentIndex = 0 // 循環
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        if (bgId != 0) {
            Image(
                painter = painterResource(bgId),
                contentDescription = "背景",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(Modifier.fillMaxSize().background(Color.White))
        }

        Row(modifier = Modifier.fillMaxSize()) {

            // 左側：2x15 圓圈
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
                                        .size(50.dp)
                                        .background(
                                            if (isActive) color else Color.LightGray.copy(alpha = 0.5f),
                                            CircleShape
                                        )
                                        .clickable(enabled = !gameEnded) {
                                            if (isActive) {
                                                scoreMap[color] = (scoreMap[color] ?: 0) + 1
                                                currentIndex += 1
                                                if (currentIndex >= totalCircles) currentIndex = 0
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

            // 右側：資訊欄與按鈕
            Column(
                modifier = Modifier
                    .width(150.dp)
                    .fillMaxHeight()
                    .padding(vertical = 40.dp, horizontal = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(25.dp)
            ) {
                GameInfoItem(label = "SCORE", value = "${scoreMap[color] ?: 0}")
                GameInfoItem(label = "TIME", value = String.format("%02d:%02d", elapsedTime / 60, elapsedTime % 60))
                GameInfoItem(label = "MISTAKES", value = "${mistakesMap[color] ?: 0}")

                Spacer(modifier = Modifier.weight(1f))

                // 重新開始（回設定頁）
                IconButton(
                    onClick = {
                        navController.navigate("level_setting_thin/$uid") {
                            popUpTo("game_thin_circle/$levelName/$uid/$practiceMinutes/$intervalSeconds") { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .size(65.dp)
                        .background(Color(0xFF4CAF50), CircleShape)
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Restart",
                        tint = Color.White,
                        modifier = Modifier.size(35.dp)
                    )
                }

                // 結束（立即結束，傳 elapsedTime）
                IconButton(
                    onClick = { finishAndGoSummary() },
                    modifier = Modifier
                        .size(65.dp)
                        .background(Color(0xFFF44336), CircleShape)
                ) {
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
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
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
