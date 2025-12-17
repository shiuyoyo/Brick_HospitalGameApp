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
import com.example.brick_hospitalgameapp.ui.screens.GameSummaryScreenMultiColor

// GameScreenMultiColor.kt
@SuppressLint("DiscouragedApi")
@Composable
fun GameScreenMultiColor(
    navController: NavController,
    userProfile: UserProfile?,
    mockUserId: String?,
    levelName: String = "關卡2",
    totalTimeSeconds: Int = 60,
    colorMode: String = "sequence" // "fixed" / "sequence" / "random"
) {
    val context = LocalContext.current
    val colors = listOf(Color.Red, Color.Yellow, Color.Blue, Color.Green)
    var scoreMap by remember { mutableStateOf(colors.associateWith { 0 }.toMutableMap()) }
    var mistakesMap by remember { mutableStateOf(colors.associateWith { 0 }.toMutableMap()) }

    var elapsedTime by remember { mutableStateOf(0) }
    var currentCircleIndex by remember { mutableStateOf(0) }
    var activeColor by remember { mutableStateOf(colors[0]) }
    var gameEnded by remember { mutableStateOf(false) }

    val totalCircles = 20
    val circles = List(totalCircles) { it }
    val circleIntervalMs = totalTimeSeconds * 1000 / totalCircles

    // 自動跳下一格，沒點擊算失誤
    LaunchedEffect(currentCircleIndex) {
        while (currentCircleIndex < totalCircles && !gameEnded) {
            delay(circleIntervalMs.toLong())
            mistakesMap[activeColor] = (mistakesMap[activeColor] ?: 0) + 1
            currentCircleIndex += 1
            if (currentCircleIndex < totalCircles) {
                activeColor = when(colorMode) {
                    "sequence" -> colors[currentCircleIndex % colors.size]
                    "random" -> colors[Random.nextInt(colors.size)]
                    else -> colors[0]
                }
            } else {
                gameEnded = true
            }
        }
    }

    // 倒計時檢查總時間，時間到自動結束遊戲
    LaunchedEffect(Unit) {
        while (!gameEnded) {
            delay(1000)
            elapsedTime += 1
            if (elapsedTime >= totalTimeSeconds) {
                gameEnded = true
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 背景
        Image(
            painter = painterResource(id = context.resources.getIdentifier("game_bg_shape","drawable",context.packageName)),
            contentDescription = "背景",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Row(modifier = Modifier.fillMaxSize().padding(55.dp)) {
            // 左側圓圈
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(30.dp)) {
                circles.chunked(5).forEachIndexed { rowIndex, row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(65.dp), verticalAlignment = Alignment.CenterVertically) {
                        row.forEachIndexed { colIndex, circle ->
                            val index = rowIndex * 5 + colIndex
                            val isActive = index == currentCircleIndex
                            val circleColor = if(isActive) activeColor else Color.LightGray
                            Box(
                                modifier = Modifier
                                    .size(150.dp)
                                    .clip(CircleShape)
                                    .background(circleColor.copy(alpha = if(isActive)0.5f else 1f))
                                    .border(width=if(isActive)3.dp else 1.dp, color=if(isActive) activeColor else Color.Gray, shape=CircleShape)
                                    .clickable(enabled=!gameEnded){
                                        if(isActive){
                                            scoreMap[activeColor] = (scoreMap[activeColor] ?:0) + 1
                                            currentCircleIndex +=1
                                            if(currentCircleIndex < totalCircles){
                                                activeColor = when(colorMode){
                                                    "sequence" -> colors[currentCircleIndex % colors.size]
                                                    "random" -> colors[Random.nextInt(colors.size)]
                                                    else -> colors[0]
                                                }
                                            } else gameEnded=true
                                        } else {
                                            mistakesMap[activeColor] = (mistakesMap[activeColor] ?:0) + 1
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ){ }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 右側資訊欄
            Column(modifier=Modifier.width(140.dp), verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.End){
                Text(text=levelName,color=Color.Black, style=MaterialTheme.typography.headlineMedium)
                Spacer(modifier=Modifier.height(70.dp))
                colors.forEach { c ->
                    Text(text="${c.getColorName()}: ${scoreMap[c]} / ${mistakesMap[c]}", color=Color.Black, fontSize=16.sp)
                    Spacer(modifier=Modifier.height(16.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 新增時間計時器
                Text(
                    text = "時間: ${elapsedTime}s",
                    color = Color.Black,
                    fontSize = 16.sp
                )

                Spacer(modifier=Modifier.height(130.dp))
                // 重新開始
                Image(
                    painter=painterResource(id=context.resources.getIdentifier("btn_restart","drawable",context.packageName)),
                    contentDescription="重新開始",
                    modifier=Modifier
                        .size(120.dp)
                        .offset(x = 50.dp, y = 40.dp)
                        .clickable {
                            navController.navigate("level_settings/$levelName/$mockUserId"){
                                popUpTo("game_multi_color/$levelName/$mockUserId/$colorMode"){inclusive=true}
                            }
                        }
                )
                Spacer(modifier=Modifier.height(16.dp))
                // 停止遊戲
                Image(
                    painter=painterResource(id=context.resources.getIdentifier("btn_end_game","drawable",context.packageName)),
                    contentDescription="停止遊戲",
                    modifier=Modifier
                        .size(120.dp)
                        .offset(x = 50.dp, y = 40.dp)
                        .clickable {
                            // 存 Map
                            val previousEntry = navController.previousBackStackEntry
                            previousEntry?.savedStateHandle?.set("scoreMap", scoreMap)
                            previousEntry?.savedStateHandle?.set("mistakesMap", mistakesMap)

                            navController.navigate("game_summary_multi_color/$levelName/$mockUserId/$totalTimeSeconds") {
                                popUpTo("game_multi_color/$levelName/$mockUserId/$colorMode") { inclusive = true }
                            }
                        }
                )
            }
        }
    }
}

// 擴展函數取得顏色名稱
fun Color.getColorName(): String = when(this){
    Color.Red -> "紅色"
    Color.Yellow -> "黃色"
    Color.Blue -> "藍色"
    Color.Green -> "綠色"
    else -> "未知"
}
