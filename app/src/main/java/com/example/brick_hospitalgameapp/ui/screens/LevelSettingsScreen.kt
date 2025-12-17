package com.example.brick_hospitalgameapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.brick_hospitalgameapp.models.UserProfile
import com.example.brick_hospitalgameapp.R
import com.example.brick_hospitalgameapp.components.PickerSelector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LevelSettingsScreen(
    navController: NavController,
    levelName: String,
    userProfile: UserProfile?,
    mockUserId: String?,
    userId: String="unknown"
) {
    val context = LocalContext.current
    val currentUserId = userProfile?.id ?: mockUserId ?: "mock_user"

    val practiceTimes = listOf(3,5,10,15,20,25,30)
    val intervalTimes = listOf(5,10,15,20,25)
    val colorModes = listOf("固定顏色", "多色順序", "多色隨機")

    var selectedPracticeTime by remember { mutableStateOf(20) }
    var selectedInterval by remember { mutableStateOf(20) }
    var selectedColorMode by remember { mutableStateOf("固定顏色") }

    val stars by remember(selectedInterval, selectedColorMode) {
        mutableStateOf(
            when(selectedInterval) {
                25 -> 1
                20 -> 2
                15 -> 3
                10 -> 4
                5 -> 5
                else -> 2
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(
                id = context.resources.getIdentifier("level_bg", "drawable", context.packageName)
            ),
            contentDescription = "背景",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

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
                        repeat(5-stars) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_star_border),
                                contentDescription = "Empty Star",
                                tint = Color.Gray,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                }

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
                        val uid = userProfile?.id ?: mockUserId ?: "guest"

                        when (selectedColorMode) {
                            "固定顏色" -> {
                                navController.navigate("game_single_color/$levelName/$uid/$selectedInterval")
                            }
                            "多色順序" -> {
                                navController.navigate("game_multi_color/$levelName/$uid/sequence/$selectedInterval")
                            }
                            "多色隨機" -> {
                                navController.navigate("game_multi_color/$levelName/$uid/random/$selectedInterval")
                            }
                            else -> {
                                navController.navigate("game_single_color/$levelName/$uid")
                            }
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
