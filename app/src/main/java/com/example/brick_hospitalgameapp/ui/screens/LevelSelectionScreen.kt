package com.example.brick_hospitalgameapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.brick_hospitalgameapp.models.ModeData
import com.example.brick_hospitalgameapp.models.UserProfile

@Composable
fun LevelSelectionScreen(
    navController: NavController,
    userProfile: UserProfile?,
    mockUserId: String?
) {
    val context = LocalContext.current
    val currentUserId = mockUserId ?: "mock_user"

    // 既有的 ModeData
    val modes = listOf(
        ModeData("關卡1", "mode1.png"),
        ModeData("關卡2", "mode2.png"),
        ModeData("關卡3", "mode3.png")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "選擇關卡",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(24.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            modes.forEach { mode ->
                val isEnabled = mode.name != "關卡3" // 關卡三暫時沒有
                val boxModifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFDBEAFE))
                    .then(
                        if (isEnabled) {
                            Modifier.clickable {
                                when (mode.name) {
                                    "關卡1" -> {
                                        // 導航到顏色遊戲的設定頁面
                                        navController.navigate("level_settings/顏色遊戲/$currentUserId")
                                    }
                                    "關卡2" -> {
                                        // 導航到形狀遊戲的設定頁面
                                        navController.navigate("level_settings_shapes/形狀遊戲/$currentUserId")
                                    }
                                }
                            }
                        } else {
                            Modifier // 不可點
                        }
                    )
                    .padding(8.dp)
                    .alpha(if (isEnabled) 1f else 0.5f)

                Box(
                    modifier = boxModifier,
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val resId = context.resources.getIdentifier(
                            mode.imageName.substringBeforeLast("."),
                            "drawable",
                            context.packageName
                        )
                        if (resId != 0) {
                            Image(
                                painter = painterResource(id = resId),
                                contentDescription = mode.name,
                                modifier = Modifier.fillMaxSize(0.7f)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isEnabled) mode.name else "${mode.name}（即將推出）",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}
