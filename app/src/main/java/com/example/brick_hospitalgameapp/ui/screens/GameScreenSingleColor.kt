package com.example.brick_hospitalgameapp.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.brick_hospitalgameapp.models.UserProfile
import kotlinx.coroutines.delay

@SuppressLint("DiscouragedApi")
@Composable
fun GameScreenSingleColor(
    navController: NavController,
    userProfile: UserProfile?,
    mockUserId: String?,
    levelName: String = "關卡1",
    totalTimeSeconds: Int = 60
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var score by remember { mutableStateOf(0) }
    var mistakes by remember { mutableStateOf(0) }
    var elapsedTime by remember { mutableStateOf(0) }
    var currentCircleIndex by remember { mutableStateOf(0) }
    var gameEnded by remember { mutableStateOf(false) }

    val totalCircles = 20
    val circles = List(totalCircles) { it }
    val circleIntervalMs = totalTimeSeconds * 1000 / totalCircles

    val navigateToSummary: () -> Unit = {
        navController.navigate(
            "game_summary/$score/$mistakes/$totalTimeSeconds/$levelName/$mockUserId"
        ) {
            popUpTo("game_single_color/$levelName/$mockUserId") { inclusive = true }
        }
    }

    // 自動跳下一格
    LaunchedEffect(currentCircleIndex) {
        if (currentCircleIndex < totalCircles && !gameEnded) {
            delay(circleIntervalMs.toLong())
            // 如果未點擊算失誤
            mistakes += 1
            currentCircleIndex += 1
            if (currentCircleIndex >= totalCircles) {
                gameEnded = true
                navigateToSummary()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 背景圖片
        Image(
            painter = painterResource(
                id = context.resources.getIdentifier("game_bg", "drawable", context.packageName)
            ),
            contentDescription = "背景",
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )

        Row(modifier = Modifier.fillMaxSize().padding(55.dp)) {
            // 左側圓圈遊戲區
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(34.dp)
            ) {
                circles.chunked(5).forEachIndexed { rowIndex, row ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(72.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        row.forEachIndexed { colIndex, circle ->
                            val index = rowIndex * 5 + colIndex
                            val isActive = index == currentCircleIndex

                            Box(
                                modifier = Modifier
                                    .size(150.dp)
                                    .clip(CircleShape)
                                    .background(if (isActive) Color(0x55FF0000) else Color.LightGray)
                                    .border(
                                        width = if (isActive) 3.dp else 1.dp,
                                        color = if (isActive) Color.Red else Color.Gray,
                                        shape = CircleShape
                                    )
                                    .clickable(enabled = !gameEnded) {
                                        if (isActive) {
                                            score += 1
                                            currentCircleIndex += 1
                                            if (currentCircleIndex >= totalCircles) {
                                                gameEnded = true
                                                navigateToSummary()
                                            }
                                        } else {
                                            mistakes += 1
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(
                                        id = context.resources.getIdentifier(
                                            "ic_circle",
                                            "drawable",
                                            context.packageName
                                        )
                                    ),
                                    contentDescription = "circle",
                                    modifier = Modifier.size(0.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp)) // 遊戲區與右側欄間距

            // 右側資訊欄
            Column(
                modifier = Modifier.width(120.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = levelName,
                    color = Color.Black,
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(70.dp))

                Text("分數: $score", color = Color.Black, style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(135.dp))

                Text("${elapsedTime} s", color = Color.Black, style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(30.dp))

                Text("失誤: $mistakes", color = Color.Black, style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(32.dp))

                // 重新開始按鈕
                Image(
                    painter = painterResource(
                        id = context.resources.getIdentifier("btn_restart", "drawable", context.packageName)
                    ),
                    contentDescription = "重新開始",
                    modifier = Modifier
                        .size(120.dp)
                        .offset(x = 30.dp, y = 15.dp)
                        .clickable {
                            navController.navigate("level_settings/$levelName/$mockUserId") {
                                popUpTo("game_single_color/$levelName/$mockUserId") { inclusive = true }
                            }
                        }
                )
                Spacer(modifier = Modifier.height(16.dp))

                // 遊戲結束按鈕
                Image(
                    painter = painterResource(
                        id = context.resources.getIdentifier("btn_end_game", "drawable", context.packageName)
                    ),
                    contentDescription = "結束遊戲",
                    modifier = Modifier
                        .size(120.dp)
                        .offset(x = 30.dp, y = 15.dp)
                        .clickable {
                            gameEnded = true
                            navigateToSummary()
                        }
                )
            }
        }
    }

    // 計時器
    LaunchedEffect(Unit) {
        while (!gameEnded) {
            delay(1000)
            elapsedTime += 1
        }
    }
}
