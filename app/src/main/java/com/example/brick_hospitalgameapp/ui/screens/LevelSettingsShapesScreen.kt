package com.example.brick_hospitalgameapp.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.brick_hospitalgameapp.R
import com.example.brick_hospitalgameapp.components.PickerSelector
import com.example.brick_hospitalgameapp.models.UserProfile
import com.example.brick_hospitalgameapp.ui.utils.drawableIdByName

@SuppressLint("DiscouragedApi")
@Composable
fun LevelSettingsShapesScreen(
    navController: NavController,
    userProfile: UserProfile?,
    mockUserId: String?,
    levelName: String = "關卡2"
) {
    val context = LocalContext.current
    val uid = userProfile?.id ?: mockUserId ?: "guest"

    val practiceTimes = listOf(3, 5, 10, 15, 20, 25, 30) // 分鐘
    val intervalTimes = listOf(5, 10, 15, 20, 25)        // 秒
    val colorModes = listOf("固定顏色", "多色順序", "多色隨機")

    var selectedPracticeTime by remember { mutableStateOf(20) } // 分鐘
    var selectedInterval by remember { mutableStateOf(20) }     // 秒
    var selectedColorMode by remember { mutableStateOf("固定顏色") }

    val stars by remember(selectedInterval, selectedColorMode) {
        mutableStateOf(
            when (selectedInterval) {
                25 -> 1
                20 -> 2
                15 -> 3
                10 -> 4
                5 -> 5
                else -> 2
            }
        )
    }

    // A 方案 drawable 防呆
    val bgId = remember { drawableIdByName(context, "bg_selectsetting2") }
    val backId = remember { drawableIdByName(context, "btn_back") }

    Box(modifier = Modifier.fillMaxSize()) {

        // 背景（防呆）
        if (bgId != 0) {
            Image(
                painter = painterResource(bgId),
                contentDescription = "背景",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        // 左上角返回按鈕
        if (backId != 0) {
            Image(
                painter = painterResource(backId),
                contentDescription = "返回關卡選擇",
                modifier = Modifier
                    .size(120.dp)
                    .padding(10.dp)
                    .clickable {
                        navController.navigate("mode_select/$uid") {
                            popUpTo("level_settings_shapes/$levelName/$uid") { inclusive = true }
                        }
                    }
            )
        } else {
            Text(
                text = "返回",
                color = Color.White,
                modifier = Modifier
                    .padding(16.dp)
                    .clickable {
                        navController.navigate("mode_select/$uid") {
                            popUpTo("level_settings_shapes/$levelName/$uid") { inclusive = true }
                        }
                    }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 76.dp, top = 120.dp, end = 170.dp, bottom = 24.dp)
        ) {
            Text(
                "關卡設定 - $levelName",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // 左側星數
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("難度星星", color = Color.White, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row {
                        repeat(stars) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_star),
                                contentDescription = "Star",
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        repeat(5 - stars) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_star_border),
                                contentDescription = "Empty Star",
                                tint = Color.Gray,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                }

                // 右側選單
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End
                ) {
                    PickerSelector(
                        label = "練習時間(分鐘)",
                        options = practiceTimes,
                        selected = selectedPracticeTime,
                        onSelect = { selectedPracticeTime = it },
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    PickerSelector(
                        label = "圖形間隔時間(秒)",
                        options = intervalTimes,
                        selected = selectedInterval,
                        onSelect = { selectedInterval = it },
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    PickerSelector(
                        label = "圓柱顏色",
                        options = colorModes,
                        selected = selectedColorMode,
                        onSelect = { selectedColorMode = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                Button(
                    onClick = {
                        val practiceMinutes = selectedPracticeTime
                        val intervalSeconds = selectedInterval

                        when (selectedColorMode) {
                            "固定顏色" -> navController.navigate(
                                "game_shapes_single/$levelName/$uid/$practiceMinutes/$intervalSeconds"
                            )
                            "多色順序" -> navController.navigate(
                                "game_shapes_multi/$levelName/$uid/sequence/$practiceMinutes/$intervalSeconds"
                            )
                            "多色隨機" -> navController.navigate(
                                "game_shapes_multi/$levelName/$uid/random/$practiceMinutes/$intervalSeconds"
                            )
                            else -> navController.navigate(
                                "game_shapes_single/$levelName/$uid/$practiceMinutes/$intervalSeconds"
                            )
                        }
                    },
                    modifier = Modifier.width(200.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("開始遊戲", color = Color.White, style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}
