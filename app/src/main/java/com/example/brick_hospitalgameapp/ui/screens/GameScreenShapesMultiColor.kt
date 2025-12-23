package com.example.brick_hospitalgameapp.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.example.brick_hospitalgameapp.ui.utils.drawableIdByName
import kotlinx.coroutines.delay
import kotlin.math.max
import kotlin.random.Random

@SuppressLint("DiscouragedApi")
@Composable
fun GameScreenShapesMultiColor(
    navController: NavController,
    userProfile: UserProfile?,
    mockUserId: String?,
    levelName: String = "關卡2",
    colorMode: String = "sequence", // sequence / random / fixed（雖然設定頁沒有 fixed，但保留彈性）
    practiceMinutes: Int = 20,
    intervalSeconds: Int = 20
) {
    val context = LocalContext.current
    val uid = userProfile?.id ?: mockUserId ?: "guest"

    val bgId = remember { drawableIdByName(context, "game_bg_shape") }
    val restartId = remember { drawableIdByName(context, "btn_restart") }
    val endId = remember { drawableIdByName(context, "btn_end_game") }

    val shapes = remember { List(15) { DefaultShapesList[it % DefaultShapesList.size] } }
    val colors = remember { listOf(Color.Red, Color.Yellow, Color.Blue, Color.Green) }

    var scoreMap by remember { mutableStateOf(colors.associateWith { 0 }.toMutableMap()) }
    var mistakesMap by remember { mutableStateOf(colors.associateWith { 0 }.toMutableMap()) }

    var elapsedTime by remember { mutableStateOf(0) }
    var currentIndex by remember { mutableStateOf(0) }
    var activeColor by remember { mutableStateOf(colors[0]) }
    var gameEnded by remember { mutableStateOf(false) }

    val totalTimeSeconds = max(1, practiceMinutes * 60)
    val intervalMs = max(1, intervalSeconds) * 1000L

    fun nextColor(nextIndex: Int): Color {
        return when (colorMode) {
            "fixed" -> colors[0]
            "random" -> colors[Random.nextInt(colors.size)]
            else -> colors[nextIndex % colors.size] // sequence
        }
    }

    fun finishAndGoSummary() {
        if (gameEnded) return
        gameEnded = true

        val target = navController.previousBackStackEntry ?: navController.currentBackStackEntry
        target?.savedStateHandle?.set("scoreMap", HashMap(scoreMap))
        target?.savedStateHandle?.set("mistakesMap", HashMap(mistakesMap))

        // 傳 elapsedTime（實際花費秒數）
        navController.navigate("game_summary_shapes/$levelName/$uid/$elapsedTime") {
            popUpTo("game_shapes_multi/$levelName/$uid/$colorMode/$practiceMinutes/$intervalSeconds") { inclusive = true }
        }
    }

    // 總時間計時
    LaunchedEffect(Unit) {
        while (!gameEnded) {
            delay(1000)
            elapsedTime += 1
            if (elapsedTime >= totalTimeSeconds) {
                finishAndGoSummary()
            }
        }
    }

    // 每 intervalSeconds 自動跳格：沒點到算錯（算在當前 activeColor）
    LaunchedEffect(currentIndex) {
        if (!gameEnded) {
            delay(intervalMs)
            mistakesMap[activeColor] = (mistakesMap[activeColor] ?: 0) + 1

            currentIndex += 1
            if (currentIndex >= shapes.size) currentIndex = 0 // 循環

            activeColor = nextColor(currentIndex)
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

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 60.dp, start = 20.dp, end = 0.dp)
                .offset(y = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            // 左側：固定 3x5
            Column(modifier = Modifier.weight(1f)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(60.dp)
                ) {
                    for (row in 0 until 3) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(40.dp)
                        ) {
                            for (col in 0 until 5) {
                                val index = row * 5 + col
                                val shape = shapes[index]
                                val isActive = index == currentIndex

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .clip(shape)
                                        .background(if (isActive) activeColor.copy(alpha = 0.5f) else Color.White)
                                        .border(
                                            width = if (isActive) 3.dp else 1.dp,
                                            color = if (isActive) activeColor else Color.Gray,
                                            shape = shape
                                        )
                                        .clickable(enabled = !gameEnded) {
                                            if (isActive) {
                                                scoreMap[activeColor] = (scoreMap[activeColor] ?: 0) + 1
                                                currentIndex += 1
                                                if (currentIndex >= shapes.size) currentIndex = 0
                                                activeColor = nextColor(currentIndex)
                                            } else {
                                                mistakesMap[activeColor] = (mistakesMap[activeColor] ?: 0) + 1
                                            }
                                        }
                                ) {}
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 右側資訊欄 + 控制
            Column(
                modifier = Modifier.width(180.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                Text(levelName, color = Color.Black, fontSize = 18.sp, style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(16.dp))

                Text("時間: ${elapsedTime} / ${totalTimeSeconds}s", color = Color.Black, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(12.dp))

                Text("間隔: ${intervalSeconds}s", color = Color.Black, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(12.dp))

                Text("目前顏色: ${activeColor.getColorName()}", color = Color.Black, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(12.dp))

                Text("分數統計:", color = Color.Black, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))

                colors.forEach { c ->
                    Text(
                        text = "${c.getColorName()}: 正確 ${scoreMap[c] ?: 0} / 錯誤 ${mistakesMap[c] ?: 0}",
                        color = Color.Black,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 重新開始
                if (restartId != 0) {
                    Image(
                        painter = painterResource(restartId),
                        contentDescription = "重新開始",
                        modifier = Modifier
                            .size(120.dp)
                            .clickable {
                                navController.navigate("level_settings_shapes/$levelName/$uid") {
                                    popUpTo("game_shapes_multi/$levelName/$uid/$colorMode/$practiceMinutes/$intervalSeconds") {
                                        inclusive = true
                                    }
                                }
                            }
                    )
                } else {
                    Button(
                        onClick = {
                            navController.navigate("level_settings_shapes/$levelName/$uid") {
                                popUpTo("game_shapes_multi/$levelName/$uid/$colorMode/$practiceMinutes/$intervalSeconds") {
                                    inclusive = true
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("重新開始") }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 結束遊戲
                if (endId != 0) {
                    Image(
                        painter = painterResource(endId),
                        contentDescription = "結束遊戲",
                        modifier = Modifier
                            .size(120.dp)
                            .clickable { finishAndGoSummary() }
                    )
                } else {
                    Button(
                        onClick = { finishAndGoSummary() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) { Text("結束遊戲", color = Color.White) }
                }
            }
        }
    }
}

// 若你專案已經有同名 extension，請刪掉這個避免衝突
fun Color.getColorName(): String = when (this) {
    Color.Red -> "紅色"
    Color.Yellow -> "黃色"
    Color.Blue -> "藍色"
    Color.Green -> "綠色"
    else -> "未知"
}
