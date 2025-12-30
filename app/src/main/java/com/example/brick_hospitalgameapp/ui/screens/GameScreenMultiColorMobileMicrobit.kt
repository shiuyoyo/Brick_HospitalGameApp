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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.brick_hospitalgameapp.microbit.MicrobitColorInput
import com.example.brick_hospitalgameapp.models.UserProfile
import com.example.brick_hospitalgameapp.ui.utils.drawableIdByName
import kotlinx.coroutines.delay
import kotlin.math.max
import kotlin.random.Random

@SuppressLint("DiscouragedApi")
@Composable
fun GameScreenMultiColorMobileMicrobit(
    navController: NavController,
    userProfile: UserProfile?,
    mockUserId: String?,
    levelName: String = "關卡測試",
    colorMode: String = "sequence", // fixed / sequence / random
    practiceMinutes: Int = 20,
    intervalSeconds: Int = 5
) {
    val context = LocalContext.current

    // micro:bit USB color input
    val microbitInput = remember { MicrobitColorInput(context) }
    DisposableEffect(Unit) {
        microbitInput.startListening()
        onDispose { microbitInput.stopListening() }
    }
    val microbitColor: Color? = microbitInput.colorState

    val bgId = remember { drawableIdByName(context, "game_bg_shape") }
    val restartId = remember { drawableIdByName(context, "btn_restart") }
    val endId = remember { drawableIdByName(context, "btn_end_game") }

    val colors = remember { listOf(Color.Red, Color.Green, Color.Blue, Color.Yellow) }
    val rows = 10
    val cols = 2
    val totalCircles = rows * cols
    val circles = remember { List(totalCircles) { it } }

    var scoreMap by remember { mutableStateOf(colors.associateWith { 0 }.toMutableMap()) }
    var mistakesMap by remember { mutableStateOf(colors.associateWith { 0 }.toMutableMap()) }

    var elapsedTime by remember { mutableStateOf(0) }
    var currentIndex by remember { mutableStateOf(0) }
    var activeColor by remember { mutableStateOf(colors[0]) }
    var gameEnded by remember { mutableStateOf(false) }

    // ✅ 兩階段：鎖色狀態
    var isLocked by remember { mutableStateOf(false) }
    var lockedColor by remember { mutableStateOf<Color?>(null) }

    val totalTimeSeconds = max(1, practiceMinutes * 60)
    val intervalMs = max(1, intervalSeconds) * 1000L
    val uid = userProfile?.id ?: mockUserId ?: "guest"

    fun nextColor(nextIndex: Int): Color = when (colorMode) {
        "fixed" -> colors[0]
        "random" -> colors[Random.nextInt(colors.size)]
        else -> colors[nextIndex % colors.size]
    }

    fun resetLock() {
        isLocked = false
        lockedColor = null
    }

    fun moveNext() {
        currentIndex = (currentIndex + 1) % totalCircles
        activeColor = nextColor(currentIndex)
        resetLock()
    }

    fun finishGame() {
        if (gameEnded) return
        gameEnded = true

        val target = navController.previousBackStackEntry ?: navController.currentBackStackEntry
        target?.savedStateHandle?.set("scoreMap", HashMap(scoreMap))
        target?.savedStateHandle?.set("mistakesMap", HashMap(mistakesMap))

        navController.navigate("game_summary_shapes/$levelName/$uid/$elapsedTime") {
            popUpTo("game_multi_color_mobile_microbit/$levelName/$uid/$colorMode/$practiceMinutes/$intervalSeconds") {
                inclusive = true
            }
        }
    }

    // 計時
    LaunchedEffect(Unit) {
        while (!gameEnded) {
            delay(1000)
            elapsedTime += 1
            if (elapsedTime >= totalTimeSeconds) finishGame()
        }
    }

    // ✅ 鎖色修正：不依賴 microbitColor 必須「變化」
    // 每一題開始（currentIndex 或 activeColor 變更）就輪詢檢查，直到鎖到為止
    LaunchedEffect(currentIndex, activeColor, gameEnded) {
        if (gameEnded) return@LaunchedEffect

        // 這裡保險起見再重置一次（避免同色切題時殘留）
        resetLock()

        while (!gameEnded && !isLocked) {
            val mb = microbitInput.colorState
            if (mb != null && mb.toArgb() == activeColor.toArgb()) {
                isLocked = true
                lockedColor = mb
                break
            }
            delay(50) // ✅ 你覺得還慢可改 20~30，但不建議 0
        }
    }

    // ✅ 每 intervalSeconds 自動跳下一題（時間到算錯）
    LaunchedEffect(currentIndex, gameEnded) {
        if (gameEnded) return@LaunchedEffect
        delay(intervalMs)

        // 時間到仍未「點擊結算」就算錯（不論有沒有鎖到）
        mistakesMap[activeColor] = (mistakesMap[activeColor] ?: 0) + 1
        moveNext()
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

        Row(modifier = Modifier.fillMaxSize().padding(16.dp)) {

            // 左側圓圈
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                circles.chunked(cols).forEachIndexed { rowIndex, row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        row.forEachIndexed { colIndex, _ ->
                            val index = rowIndex * cols + colIndex
                            val isActive = index == currentIndex

                            // 視覺提示：active 未鎖色淡、鎖色亮
                            val alpha = if (!isActive) 1f else if (isLocked) 0.85f else 0.45f
                            val circleColor = if (isActive) activeColor.copy(alpha = alpha) else Color.LightGray
                            val borderWidth = if (isActive) (if (isLocked) 4.dp else 2.dp) else 1.dp
                            val borderColor = if (isActive) activeColor else Color.Gray

                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(circleColor)
                                    .border(borderWidth, borderColor, CircleShape)
                                    .clickable(enabled = !gameEnded) {
                                        if (!isActive) return@clickable

                                        // ✅ 你指定的規則：未鎖色就點 → 算錯並換下一題
                                        if (isLocked) {
                                            scoreMap[activeColor] = (scoreMap[activeColor] ?: 0) + 1
                                            moveNext()
                                        } else {
                                            mistakesMap[activeColor] = (mistakesMap[activeColor] ?: 0) + 1
                                            moveNext()
                                        }
                                    }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 右側資訊 + 控制
            Column(
                modifier = Modifier.width(220.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                Text(levelName, color = Color.Black, style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(10.dp))

                Text("時間: $elapsedTime / $totalTimeSeconds s", color = Color.Black, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(10.dp))

                Text("Active: ${activeColor.getColorName()}", color = Color.Black, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(6.dp))

                val microbitText = microbitColor?.getColorName() ?: "未偵測"
                Text("micro:bit: $microbitText", color = Color.Black, fontSize = 16.sp)

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (isLocked) "狀態: 已鎖色（可點擊得分）" else "狀態: 等待鎖色…（只鎖 Active 顏色）",
                    color = Color.Black,
                    fontSize = 14.sp
                )

                // ✅ 你指定：一定要放重新偵測 USB + 狀態顯示
                Spacer(modifier = Modifier.height(10.dp))
                Text("USB 狀態:", color = Color.Black, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                        .border(1.dp, Color.Gray)
                        .padding(8.dp)
                ) {
                    Text(
                        text = microbitInput.statusText,
                        color = Color.Black,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = { microbitInput.rescan() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("重新掃描 USB")
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text("分數統計:", color = Color.Black, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(6.dp))
                colors.forEach { c ->
                    Text(
                        "${c.getColorName()}: 正確 ${scoreMap[c] ?: 0} / 錯誤 ${mistakesMap[c] ?: 0}",
                        color = Color.Black,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                Spacer(modifier = Modifier.height(18.dp))

                if (restartId != 0) {
                    Image(
                        painter = painterResource(restartId),
                        contentDescription = "重新開始",
                        modifier = Modifier
                            .size(100.dp)
                            .clickable {
                                navController.navigate("mode_select/$uid") {
                                    popUpTo("game_multi_color_mobile_microbit/$levelName/$uid/$colorMode/$practiceMinutes/$intervalSeconds") {
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
                            .size(100.dp)
                            .clickable { finishGame() }
                    )
                }
            }
        }
    }
}
