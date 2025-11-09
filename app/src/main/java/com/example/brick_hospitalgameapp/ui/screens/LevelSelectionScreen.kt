package com.example.brick_hospitalgameapp.screens

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
    val currentUserId = userProfile?.id ?: mockUserId ?: "mock_user"

    // 使用專案中已經定義好的 ModeData
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
        // 顯示歡迎文字
        Text(
            text = if (userProfile != null) {
                "歡迎, ${userProfile.full_name ?: userProfile.username ?: "訪客"}"
            } else {
                "歡迎, 訪客"
            },
            style = MaterialTheme.typography.headlineSmall,
            color = Color.Black, // 可依背景改成 Color.White
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        Text("選擇關卡", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            modes.forEach { mode ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFDBEAFE))
                        .clickable {
                            // 點擊整個關卡區塊 → LevelSettingsScreen
                            navController.navigate("level_settings/${mode.name}/$currentUserId")
                        }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(
                                id = context.resources.getIdentifier(
                                    mode.imageName.substringBeforeLast("."),
                                    "drawable",
                                    context.packageName
                                )
                            ),
                            contentDescription = mode.name,
                            modifier = Modifier.fillMaxSize(0.7f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(mode.name, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}
