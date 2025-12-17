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
import kotlinx.coroutines.delay

@SuppressLint("DiscouragedApi")
@Composable
fun GameScreenShapesSingle(
    navController: NavController,
    levelName: String,
    mockUserId: String?,
    totalTimeSeconds: Int = 60,
    userProfile: UserProfile?
) {
    val context = LocalContext.current
    val color = Color.Red

    // 創建15個形狀（3行5列）
    val shapes = remember {
        List(15) { index ->
            DefaultShapesList[index % DefaultShapesList.size]
        }
    }

    var scoreMap by remember { mutableStateOf(mutableMapOf(color to 0)) }
    var mistakesMap by remember { mutableStateOf(mutableMapOf(color to 0)) }
    var elapsedTime by remember { mutableStateOf(0) }
    var currentIndex by remember { mutableStateOf(0) }
    var gameEnded by remember { mutableStateOf(false) }

    val intervalMs = totalTimeSeconds * 1000 / shapes.size

    // 自動下一格
    LaunchedEffect(currentIndex) {
        if (currentIndex < shapes.size && !gameEnded) {
            delay(intervalMs.toLong())
            mistakesMap[color] = (mistakesMap[color] ?: 0) + 1
            currentIndex += 1
            if (currentIndex >= shapes.size) {
                gameEnded = true
                navController.currentBackStackEntry?.savedStateHandle?.set("scoreMap", scoreMap)
                navController.currentBackStackEntry?.savedStateHandle?.set("mistakesMap", mistakesMap)
                navController.navigate("game_summary_shapes/$levelName/$mockUserId/$totalTimeSeconds") {
                    popUpTo("game_shapes_single/$levelName/$mockUserId/$totalTimeSeconds") {
                        inclusive = true
                    }
                }
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
                    columns = GridCells.Fixed(5),
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(40.dp),
                    verticalArrangement = Arrangement.spacedBy(60.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    itemsIndexed(shapes) { index, shape ->
                        val isActive = index == currentIndex


                        // 顏色示範：保持單色紅色，也可以依照 index 自行改變
                        val boxColor = if (isActive) Color.Red.copy(alpha = 0.5f) else Color.White

                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clip(shape)
                                .background(boxColor)
                                .border(
                                    width = if (isActive) 3.dp else 1.dp,
                                    color = if (isActive) Color.Red else Color.Gray,
                                    shape = shape
                                )
                                .clickable(enabled = !gameEnded) {
                                    if (isActive) {
                                        scoreMap[color] = (scoreMap[color] ?: 0) + 1
                                        currentIndex += 1
                                        if (currentIndex >= shapes.size) gameEnded = true
                                    } else {
                                        mistakesMap[color] = (mistakesMap[color] ?: 0) + 1
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
                    text = "進度: ${currentIndex}/${shapes.size}",
                    color = Color.Black,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 當前提示
                if (!gameEnded) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = color.copy(alpha = 0.3f)
                        )
                    ) {
                        Text(
                            text = "點擊紅色\n高亮的形狀",
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

                Text(
                    text = "正確: ${scoreMap[color]}",
                    color = Color.Green,
                    fontSize = 14.sp
                )

                Text(
                    text = "錯誤: ${mistakesMap[color]}",
                    color = Color.Red,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(30.dp))

                // 控制按鈕
                Button(
                    onClick = {
                        navController.navigate("level_settings_shapes/$levelName/$mockUserId") {
                            popUpTo("game_shapes_single/$levelName/$mockUserId/$totalTimeSeconds") {
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
                            popUpTo("game_shapes_single/$levelName/$mockUserId/$totalTimeSeconds") {
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