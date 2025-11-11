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
import com.example.brick_hospitalgameapp.microbit.MicrobitColorInput
import com.example.brick_hospitalgameapp.models.UserProfile
import kotlinx.coroutines.delay
import kotlin.random.Random

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
    val scope = rememberCoroutineScope()

    val colors = listOf(Color.Red, Color.Yellow, Color.Blue, Color.Green)
    var scoreMap by remember { mutableStateOf(mutableMapOf<Color, Int>()) }
    var mistakesMap by remember { mutableStateOf(mutableMapOf<Color, Int>()) }

    colors.forEach {
        scoreMap[it] = 0
        mistakesMap[it] = 0
    }

    var elapsedTime by remember { mutableStateOf(0) }
    var currentCircleIndex by remember { mutableStateOf(0) }
    var activeColor by remember { mutableStateOf<Color>(Color.Red) }
    var gameEnded by remember { mutableStateOf(false) }

    val totalCircles = 20
    val circles = List(totalCircles) { it }
    val circleIntervalMs = totalTimeSeconds * 1000 / totalCircles

    // micro:bit
    val microbit = remember { MicrobitColorInput(context) }
    DisposableEffect(Unit) {
        microbit.startListening()
        onDispose { microbit.stopListening() }
    }

    LaunchedEffect(currentCircleIndex) {
        if (currentCircleIndex < totalCircles && !gameEnded) {
            delay(circleIntervalMs.toLong())
            // 如果沒點擊就算失誤
            mistakesMap[activeColor] = (mistakesMap[activeColor] ?: 0) + 1
            currentCircleIndex += 1
            activeColor = when(colorMode) {
                "sequence" -> colors[currentCircleIndex % colors.size]
                "random" -> colors[Random.nextInt(colors.size)]
                else -> Color.Red
            }
            if (currentCircleIndex >= totalCircles) gameEnded = true
        }
    }

    // 監聽 microbit
    LaunchedEffect(microbit.colorState) {
        microbit.colorState?.let { inputColor ->
            if (gameEnded) return@LaunchedEffect
            if (inputColor == activeColor) {
                scoreMap[activeColor] = (scoreMap[activeColor] ?: 0) + 1
                currentCircleIndex += 1
                activeColor = when(colorMode) {
                    "sequence" -> colors[currentCircleIndex % colors.size]
                    "random" -> colors[Random.nextInt(colors.size)]
                    else -> Color.Red
                }
                if (currentCircleIndex >= totalCircles) gameEnded = true
            } else {
                mistakesMap[inputColor] = (mistakesMap[inputColor] ?: 0) + 1
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = context.resources.getIdentifier("game_bg", "drawable", context.packageName)),
            contentDescription = "背景",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Row(modifier = Modifier.fillMaxSize().padding(40.dp)) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(30.dp)
            ) {
                circles.chunked(5).forEachIndexed { rowIndex, row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(60.dp), verticalAlignment = Alignment.CenterVertically) {
                        row.forEachIndexed { colIndex, circle ->
                            val index = rowIndex * 5 + colIndex
                            val isActive = index == currentCircleIndex
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .background(if (isActive) activeColor.copy(alpha = 0.5f) else Color.LightGray)
                                    .border(width = if (isActive) 3.dp else 1.dp,
                                        color = if (isActive) activeColor else Color.Gray, shape = CircleShape)
                                    .clickable(enabled = !gameEnded) {
                                        if (isActive) {
                                            scoreMap[activeColor] = (scoreMap[activeColor] ?: 0) + 1
                                            currentCircleIndex += 1
                                            activeColor = when(colorMode) {
                                                "sequence" -> colors[currentCircleIndex % colors.size]
                                                "random" -> colors[Random.nextInt(colors.size)]
                                                else -> Color.Red
                                            }
                                            if (currentCircleIndex >= totalCircles) gameEnded = true
                                        } else {
                                            mistakesMap[activeColor] = (mistakesMap[activeColor] ?: 0) + 1
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) { /* 可放圖案 */ }
                        }
                    }
                }
            }

            // 右側資訊欄可自行加
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
