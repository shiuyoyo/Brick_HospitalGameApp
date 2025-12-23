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

@SuppressLint("DiscouragedApi")
@Composable
fun GameScreenShapesSingle(
    navController: NavController,
    userProfile: UserProfile?,
    mockUserId: String?,
    levelName: String = "關卡2",
    practiceMinutes: Int = 20,
    intervalSeconds: Int = 20
) {
    val context = LocalContext.current
    val uid = userProfile?.id ?: mockUserId ?: "guest"

    // A 方案 drawable 防呆
    val bgId = remember { drawableIdByName(context, "game_bg_shape") }
    val restartId = remember { drawableIdByName(context, "btn_restart") }
    val endId = remember { drawableIdByName(context, "btn_end_game") }

    // 3x5 = 15 shapes
    val shapes = remember { List(15) { DefaultShapesList[it % DefaultShapesList.size] } }
    val activeColor = Color.Red

    // 分數（只用紅色一個 key，維持你 summary 使用 scoreMap/mistakesMap 的格式）
    var scoreMap by remember { mutableStateOf(mutableMapOf(activeColor to 0)) }
    var mistakesMap by remember { mutableStateOf(mutableMapOf(activeColor to 0)) }

    var elapsedTime by remember { mutableStateOf(0) }
    var currentIndex by remember { mutableStateOf(0) }
    var gameEnded by remember { mutableStateOf(false) }

    val totalTimeSeconds = max(1, practiceMinutes * 60)
    val intervalMs = max(1, intervalSeconds) * 1000L

    fun finishAndGoSummary() {
        if (gameEnded) return
        gameEnded = true

        // 存到上一頁 entry，避免 popUpTo inclusive 讓 summary 取不到
        val target = navController.previousBackStackEntry ?: navController.currentBackStackEntry
        target?.savedStateHandle?.set("scoreMap", HashMap(scoreMap))
        target?.savedStateHandle?.set("mistakesMap", HashMap(mistakesMap))

        // 傳「實際花費時間 elapsedTime」
        navController.navigate("game_summary_shapes/$levelName/$uid/$elapsedTime") {
            popUpTo("game_shapes_single/$levelName/$uid/$practiceMinutes/$intervalSeconds") { inclusive = true }
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

    // 每 intervalSeconds 自動跳格：沒點到算一次錯
    LaunchedEffect(currentIndex) {
        if (!gameEnded) {
            delay(intervalMs)
            mistakesMap[activeColor] = (mistakesMap[activeColor] ?: 0) + 1
            currentIndex += 1
            if (currentIndex >= shapes.size) currentIndex = 0 // 循環
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // 背景
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

            // 左側：固定 3x5（不用 LazyVerticalGrid）
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

            // 右側：資訊欄 + 控制
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

                Text("正確: ${scoreMap[activeColor] ?: 0}", color = Color(0xFF2E7D32), fontSize = 16.sp)
                Text("錯誤: ${mistakesMap[activeColor] ?: 0}", color = Color(0xFFC62828), fontSize = 16.sp)

                Spacer(modifier = Modifier.height(20.dp))

                // 重新開始
                if (restartId != 0) {
                    Image(
                        painter = painterResource(restartId),
                        contentDescription = "重新開始",
                        modifier = Modifier
                            .size(120.dp)
                            .clickable {
                                navController.navigate("level_settings_shapes/$levelName/$uid") {
                                    popUpTo("game_shapes_single/$levelName/$uid/$practiceMinutes/$intervalSeconds") {
                                        inclusive = true
                                    }
                                }
                            }
                    )
                } else {
                    Button(
                        onClick = {
                            navController.navigate("level_settings_shapes/$levelName/$uid") {
                                popUpTo("game_shapes_single/$levelName/$uid/$practiceMinutes/$intervalSeconds") {
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
