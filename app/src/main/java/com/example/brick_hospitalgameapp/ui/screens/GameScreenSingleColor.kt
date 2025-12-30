package com.example.brick_hospitalgameapp.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.brick_hospitalgameapp.models.UserProfile
import com.example.brick_hospitalgameapp.ui.utils.drawableIdByName
import kotlinx.coroutines.delay
import kotlin.math.max

@SuppressLint("DiscouragedApi")
@Composable
fun GameScreenSingleColor(
    navController: NavController,
    userProfile: UserProfile?,
    mockUserId: String?,
    levelName: String = "關卡1",
    practiceMinutes: Int = 20,
    intervalSeconds: Int = 20
) {
    val context = LocalContext.current

    val bgId = remember { drawableIdByName(context, "game_bg_shape") }
    val circleIconId = remember { drawableIdByName(context, "ic_circle") }
    val restartId = remember { drawableIdByName(context, "btn_restart") }
    val endId = remember { drawableIdByName(context, "btn_end_game") }

    var score by remember { mutableStateOf(0) }
    var mistakes by remember { mutableStateOf(0) }
    var elapsedTime by remember { mutableStateOf(0) }
    var currentCircleIndex by remember { mutableStateOf(0) }
    var gameEnded by remember { mutableStateOf(false) }

    val totalCircles = 15
    val circles = remember { List(totalCircles) { it } }

    val totalTimeSeconds = max(1, practiceMinutes * 60)
    val intervalMs = max(1, intervalSeconds) * 1000L

    val uid = userProfile?.id ?: mockUserId ?: "mock_user"

    fun navigateToSummary() {
        navController.navigate(
            "game_summary/$score/$mistakes/$elapsedTime/$levelName/$uid"
        ) {
            popUpTo("game_single_color/$levelName/$uid/$practiceMinutes/$intervalSeconds") { inclusive = true }
        }
    }

    // 總時間計時
    LaunchedEffect(Unit) {
        while (!gameEnded) {
            delay(1000)
            elapsedTime += 1
            if (elapsedTime >= totalTimeSeconds) {
                gameEnded = true
                navigateToSummary()
            }
        }
    }

    // 每 intervalSeconds 自動跳一格（沒點到就算一次錯）
    LaunchedEffect(currentCircleIndex) {
        if (!gameEnded) {
            delay(intervalMs)
            mistakes += 1
            currentCircleIndex += 1
            if (currentCircleIndex >= totalCircles) {
                currentCircleIndex = 0 // 循環
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        if (bgId != 0) {
            Image(
                painter = painterResource(bgId),
                contentDescription = "背景",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(Modifier.fillMaxSize().background(Color.White))
        }

        Row(modifier = Modifier.fillMaxSize().padding(55.dp, vertical = 100.dp)) {

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(70.dp)
            ) {
                circles.chunked(5).forEachIndexed { rowIndex, row ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(60.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        row.forEachIndexed { colIndex, _ ->
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
                                            // 點到後直接跳下一格（不等待 timer）
                                            currentCircleIndex += 1
                                            if (currentCircleIndex >= totalCircles) currentCircleIndex = 0
                                        } else {
                                            mistakes += 1
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (circleIconId != 0) {
                                    Image(
                                        painter = painterResource(circleIconId),
                                        contentDescription = "circle",
                                        modifier = Modifier.size(0.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.width(140.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.End
            ) {
                Text(text = levelName, color = Color.Black, style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(30.dp))

                Text("分數: $score", color = Color.Black, style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(30.dp))

                Text("${elapsedTime} / ${totalTimeSeconds}s", color = Color.Black, style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(20.dp))

                Text("失誤: $mistakes", color = Color.Black, style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(24.dp))

                if (restartId != 0) {
                    Image(
                        painter = painterResource(restartId),
                        contentDescription = "重新開始",
                        modifier = Modifier
                            .size(120.dp)
                            .offset(x = 30.dp, y = 15.dp)
                            .clickable {
                                navController.navigate("level_settings/$levelName/$uid") {
                                    popUpTo("game_single_color/$levelName/$uid/$practiceMinutes/$intervalSeconds") {
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
                            .offset(x = 30.dp, y = 15.dp)
                            .clickable {
                                gameEnded = true
                                navigateToSummary()
                            }
                    )
                }
            }
        }
    }
}
