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
import kotlin.random.Random

@SuppressLint("DiscouragedApi", "UnrememberedMutableState")
@Composable
fun GameScreenMultiColor(
    navController: NavController,
    userProfile: UserProfile?,
    mockUserId: String?,
    levelName: String = "關卡1",
    totalTimeSeconds: Int = 60,
    colorMode: String = "固定顏色" // "固定顏色", "多色順序", "多色隨機"
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

    // 四種顏色
    val colorList = listOf(Color.Yellow, Color.Blue, Color.Green, Color.Red)

    // 記錄每個顏色的正確/錯誤數量
    val correctColorCount = remember { mutableStateMapOf<Color, Int>() }
    val wrongColorCount = remember { mutableStateMapOf<Color, Int>() }
    colorList.forEach {
        correctColorCount[it] = 0
        wrongColorCount[it] = 0
    }

    // 產生圓圈亮的顏色
    val activeColor by derivedStateOf {
        when (colorMode) {
            "固定顏色" -> Color.Red
            "多色順序" -> colorList[currentCircleIndex % colorList.size]
            "多色隨機" -> colorList[Random.nextInt(colorList.size)]
            else -> Color.Red
        }
    }


    // 自動跳下一格，沒點擊算失誤
    LaunchedEffect(currentCircleIndex) {
        if (currentCircleIndex < totalCircles && !gameEnded) {
            delay(circleIntervalMs.toLong())
            // 沒點擊，算失誤
            wrongColorCount[activeColor] = (wrongColorCount[activeColor] ?: 0) + 1
            currentCircleIndex += 1
            if (currentCircleIndex >= totalCircles) gameEnded = true
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 背景
        Image(
            painter = painterResource(id = context.resources.getIdentifier("game_bg", "drawable", context.packageName)),
            contentDescription = "背景",
            contentScale = ContentScale.Crop,
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
                                    .background(if (isActive) activeColor.copy(alpha = 0.3f) else Color.LightGray)
                                    .border(
                                        width = if (isActive) 3.dp else 1.dp,
                                        color = if (isActive) activeColor else Color.Gray,
                                        shape = CircleShape
                                    )
                                    .clickable(enabled = !gameEnded) {
                                        if (isActive) {
                                            score += 1
                                            correctColorCount[activeColor] = (correctColorCount[activeColor] ?: 0) + 1
                                            currentCircleIndex += 1
                                            if (currentCircleIndex >= totalCircles) gameEnded = true
                                        } else {
                                            mistakes += 1
                                            val wrongClickedColor = if (colorMode=="固定顏色") Color.Red
                                            else colorList[Random.nextInt(colorList.size)]
                                            wrongColorCount[wrongClickedColor] = (wrongColorCount[wrongClickedColor] ?: 0) + 1
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                // 可放圖
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 右側資訊欄
            Column(
                modifier = Modifier.width(120.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.End
            ) {
                Text(levelName, color = Color.Black, style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(70.dp))
                Text("分數: $score", color = Color.Black, style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(135.dp))
                Text("時間: ${elapsedTime}s", color = Color.Black, style = MaterialTheme.typography.headlineMedium)
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
                            navController.navigate(
                                "game_summary_multi/$score/$mistakes/$totalTimeSeconds/$levelName/$mockUserId/${correctColorCount[Color.Yellow]}/${wrongColorCount[Color.Yellow]}/${correctColorCount[Color.Blue]}/${wrongColorCount[Color.Blue]}/${correctColorCount[Color.Green]}/${wrongColorCount[Color.Green]}/${correctColorCount[Color.Red]}/${wrongColorCount[Color.Red]}"
                            ) {
                                popUpTo("game_single_color/$levelName/$mockUserId") { inclusive = true }
                            }
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
