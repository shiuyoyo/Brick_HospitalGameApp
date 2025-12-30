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
import com.example.brick_hospitalgameapp.ui.utils.drawableIdByName
import kotlinx.coroutines.delay
import kotlin.math.max
import kotlin.random.Random

@SuppressLint("DiscouragedApi")
@Composable
fun GameScreenMultiColor(
    navController: NavController,
    userProfile: UserProfile?,
    mockUserId: String?,
    levelName: String = "關卡2",
    colorMode: String = "sequence", // fixed / sequence / random
    practiceMinutes: Int = 20,
    intervalSeconds: Int = 20
) {
    val context = LocalContext.current

    val bgId = remember { drawableIdByName(context, "game_bg_shape") }
    val restartId = remember { drawableIdByName(context, "btn_restart") }
    val endId = remember { drawableIdByName(context, "btn_end_game") }

    val colors = remember { listOf(Color.Red, Color.Yellow, Color.Blue, Color.Green) }
    val totalCircles = 20
    val circles = remember { List(totalCircles) { it } }

    var scoreMap by remember { mutableStateOf(colors.associateWith { 0 }.toMutableMap()) }
    var mistakesMap by remember { mutableStateOf(colors.associateWith { 0 }.toMutableMap()) }

    var elapsedTime by remember { mutableStateOf(0) }
    var currentCircleIndex by remember { mutableStateOf(0) }
    var activeColor by remember { mutableStateOf(colors[0]) }
    var gameEnded by remember { mutableStateOf(false) }

    val totalTimeSeconds = max(1, practiceMinutes * 60)
    val intervalMs = max(1, intervalSeconds) * 1000L

    val uid = userProfile?.id ?: mockUserId ?: "mock_user"

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

        // 存到上一頁 entry（避免你 pop 掉遊戲頁後 summary 取不到）
        val target = navController.previousBackStackEntry ?: navController.currentBackStackEntry
        target?.savedStateHandle?.set("scoreMap", HashMap(scoreMap))
        target?.savedStateHandle?.set("mistakesMap", HashMap(mistakesMap))

        navController.navigate("game_summary_multi_color/$levelName/$uid/$elapsedTime") {
            popUpTo("game_multi_color/$levelName/$uid/$colorMode/$practiceMinutes/$intervalSeconds") { inclusive = true }
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

    // 每 intervalSeconds 自動跳格（沒點到算錯：算在當前 activeColor）
    LaunchedEffect(currentCircleIndex) {
        if (!gameEnded) {
            delay(intervalMs)
            mistakesMap[activeColor] = (mistakesMap[activeColor] ?: 0) + 1

            currentCircleIndex += 1
            if (currentCircleIndex >= totalCircles) currentCircleIndex = 0

            activeColor = nextColor(currentCircleIndex)
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

        Row(modifier = Modifier.fillMaxSize().padding(55.dp, vertical = 100.dp)) {

            // 左側圓圈
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(70.dp)
            ) {
                circles.chunked(5).forEachIndexed { rowIndex, row ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(50.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        row.forEachIndexed { colIndex, _ ->
                            val index = rowIndex * 5 + colIndex
                            val isActive = index == currentCircleIndex
                            val circleColor = if (isActive) activeColor.copy(alpha = 0.5f) else Color.LightGray

                            Box(
                                modifier = Modifier
                                    .size(150.dp)
                                    .clip(CircleShape)
                                    .background(circleColor)
                                    .border(
                                        width = if (isActive) 3.dp else 1.dp,
                                        color = if (isActive) activeColor else Color.Gray,
                                        shape = CircleShape
                                    )
                                    .clickable(enabled = !gameEnded) {
                                        if (isActive) {
                                            scoreMap[activeColor] = (scoreMap[activeColor] ?: 0) + 1
                                            currentCircleIndex += 1
                                            if (currentCircleIndex >= totalCircles) currentCircleIndex = 0
                                            activeColor = nextColor(currentCircleIndex)
                                        } else {
                                            mistakesMap[activeColor] = (mistakesMap[activeColor] ?: 0) + 1
                                        }
                                    }
                            ) {}
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 右側資訊欄 + 控制
            Column(
                modifier = Modifier.width(200.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.End
            ) {
                Text(levelName, color = Color.Black, style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))

                Text("時間: ${elapsedTime} / ${totalTimeSeconds}s", color = Color.Black, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(12.dp))

                Text("目前顏色: ${activeColor.getColorName()}", color = Color.Black, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(16.dp))

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

                Spacer(modifier = Modifier.height(20.dp))

                if (restartId != 0) {
                    Image(
                        painter = painterResource(restartId),
                        contentDescription = "重新開始",
                        modifier = Modifier
                            .size(120.dp)
                            .offset(x = 40.dp, y = 10.dp)
                            .clickable {
                                navController.navigate("level_settings/$levelName/$uid") {
                                    popUpTo("game_multi_color/$levelName/$uid/$colorMode/$practiceMinutes/$intervalSeconds") {
                                        inclusive = true
                                    }
                                }
                            }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (endId != 0) {
                    Image(
                        painter = painterResource(endId),
                        contentDescription = "結束遊戲",
                        modifier = Modifier
                            .size(120.dp)
                            .offset(x = 40.dp, y = 10.dp)
                            .clickable { finishAndGoSummary() }
                    )
                } else {
                    Button(
                        onClick = { finishAndGoSummary() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) { Text("結束遊戲", color = Color.White) }
                }
            }
        }
    }
}

