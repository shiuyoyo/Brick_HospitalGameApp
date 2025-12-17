package com.example.brick_hospitalgameapp.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
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
import com.example.brick_hospitalgameapp.ui.shapes.DefaultShapesList
import com.example.brick_hospitalgameapp.utils.getColorName
import kotlinx.coroutines.delay
import kotlin.random.Random

@SuppressLint("DiscouragedApi")
@Composable
fun GameScreenShapesMultiColor(
    navController: NavController,
    levelName: String,
    mockUserId: String?,
    totalTimeSeconds: Int = 60,
    colorMode: String = "sequence",
    userProfile: UserProfile?
) {
    val context = LocalContext.current
    val colors = listOf(Color.Red, Color.Yellow, Color.Blue, Color.Green)

    // 創建15個形狀（3行5列）
    val shapes = remember {
        List(15) { index ->
            DefaultShapesList[index % DefaultShapesList.size]
        }
    }

    var scoreMap by remember { mutableStateOf(colors.associateWith { 0 }.toMutableMap()) }
    var mistakesMap by remember { mutableStateOf(colors.associateWith { 0 }.toMutableMap()) }

    var elapsedTime by remember { mutableStateOf(0) }
    var currentIndex by remember { mutableStateOf(0) }
    var activeColor by remember { mutableStateOf(colors[0]) }
    var gameEnded by remember { mutableStateOf(false) }

    val totalShapes = shapes.size
    val intervalMs = totalTimeSeconds * 1000 / totalShapes

    // 自動跳下一格
    LaunchedEffect(currentIndex) {
        if (currentIndex < totalShapes && !gameEnded) {
            delay(intervalMs.toLong())
            mistakesMap[activeColor] = (mistakesMap[activeColor] ?: 0) + 1
            currentIndex += 1
            if (currentIndex < totalShapes) {
                activeColor = when (colorMode) {
                    "sequence" -> colors[currentIndex % colors.size]
                    "random" -> colors[Random.nextInt(colors.size)]
                    else -> colors[0]
                }
            } else {
                gameEnded = true
                navController.currentBackStackEntry?.savedStateHandle?.set("scoreMap", scoreMap)
                navController.currentBackStackEntry?.savedStateHandle?.set("mistakesMap", mistakesMap)

                navController.navigate("game_summary_shapes/$levelName/$mockUserId/$totalTimeSeconds") {
                    popUpTo("game_shapes_multi/$levelName/$mockUserId/$colorMode") { inclusive = true }
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 背景
        Image(
            painter = painterResource(
                id = context.resources.getIdentifier("game_bg_shape", "drawable", context.packageName)
            ),
            contentDescription = "背景",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 60.dp, start = 20.dp, end = 0.dp) // 上方增加 40.dp 空間
                .offset( y = 20.dp), // 或使用 offset 讓整個 Row 往下 20.dp

            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 左側：5x3 形狀網格
            Column(modifier = Modifier.weight(1f)) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(5), // 5個一排
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(40.dp),
                    verticalArrangement = Arrangement.spacedBy(60.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    itemsIndexed(shapes) { index, shape ->
                        val isActive = index == currentIndex
                        val shapeColor = if (isActive) activeColor.copy(alpha = 0.5f) else Color.White

                        Box(
                            modifier = Modifier
                                .aspectRatio(1f) // 保持正方形
                                .clip(shape)
                                .background(shapeColor.copy(alpha = if (isActive) 0.5f else 1f))
                                .border(
                                    width = if (isActive) 3.dp else 1.dp,
                                    color = if (isActive) activeColor else Color.Gray,
                                    shape = shape
                                )
                                .clickable(enabled = !gameEnded) {
                                    if (isActive) {
                                        scoreMap[activeColor] = (scoreMap[activeColor] ?: 0) + 1
                                        currentIndex += 1
                                        if (currentIndex < totalShapes) {
                                            activeColor = when (colorMode) {
                                                "sequence" -> colors[currentIndex % colors.size]
                                                "random" -> colors[Random.nextInt(colors.size)]
                                                else -> colors[0]
                                            }
                                        } else {
                                            gameEnded = true
                                        }
                                    } else {
                                        mistakesMap[activeColor] = (mistakesMap[activeColor] ?: 0) + 1
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {}
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 右側：資訊欄
            Column(
                modifier = Modifier.width(200.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = levelName,
                    color = Color.Black,
                    style = MaterialTheme.typography.headlineSmall,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "時間: ${elapsedTime}s",
                    color = Color.Black,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "進度: ${currentIndex}/${totalShapes}",
                    color = Color.Black,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 當前顏色指示
                if (!gameEnded) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = activeColor.copy(alpha = 0.3f)
                        )
                    ) {
                        Text(
                            text = "當前顏色:\n${activeColor.getColorName()}",
                            color = Color.Black,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 分數統計
                Text(
                    text = "分數統計:",
                    color = Color.Black,
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.bodyMedium
                )

                colors.forEach { c ->
                    Text(
                        text = "${c.getColorName()}: ${scoreMap[c]} / ${mistakesMap[c]}",
                        color = Color.Black,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                Spacer(modifier = Modifier.height(30.dp))

                // 控制按鈕
                Button(
                    onClick = {
                        navController.navigate("level_settings_shapes/$levelName/$mockUserId") {
                            popUpTo("game_shapes_multi/$levelName/$mockUserId/$colorMode") {
                                inclusive = true
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
                ) {
                    Text("重新開始", color = Color.White)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        navController.currentBackStackEntry?.savedStateHandle?.set("scoreMap", scoreMap)
                        navController.currentBackStackEntry?.savedStateHandle?.set("mistakesMap", mistakesMap)

                        navController.navigate("game_summary_shapes/$levelName/$mockUserId/$totalTimeSeconds") {
                            popUpTo("game_shapes_multi/$levelName/$mockUserId/$colorMode") {
                                inclusive = true
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("結束遊戲", color = Color.White)
                }
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