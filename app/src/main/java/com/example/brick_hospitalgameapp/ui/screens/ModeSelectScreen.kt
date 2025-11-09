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
import com.example.brick_hospitalgameapp.models.UserProfile // 確保這個模型可以被訪問

data class ModeData(val name: String, val imageName: String)

@Composable
fun ModeSelectScreen(
    navController: NavController,
    userProfile: UserProfile?,
    mockUserId: String?,
    // ** 【關鍵】新增此參數以解決被 Scaffold 頂部欄遮擋的問題 **
    paddingValues: PaddingValues = PaddingValues(0.dp)
) {
    val context = LocalContext.current
    val modes = listOf(
        ModeData("關卡1", "mode1.png"),
        ModeData("關卡2", "mode2.png"),
        ModeData("關卡3", "mode3.png")
    )

    // 使用者ID：Supabase回傳或mockUser
    val currentUserId = userProfile?.id ?: mockUserId ?: "unknown"

    Column(
        modifier = Modifier
            // ** 應用外部傳入的 paddingValues (解決遮擋) **
            .padding(paddingValues)
            .fillMaxSize()
            // ** 應用額外的左右 padding **
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ------------------
        // [1] 顯示使用者名稱 (位於頂部，新增 top padding)
        // ------------------
        Text(
            text = if (userProfile != null) {
                "歡迎, ${userProfile.full_name ?: userProfile.username ?: "訪客"}"
            } else {
                "歡迎, 訪客"
            },
            color = Color.Black,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 8.dp) // 確保與頂部有間距，且下方有小間距
        )

        // ------------------
        // [2] ** 【新增】顯示資料庫代出的 ID **
        // ------------------
        Text(
            text = "使用者 ID: $currentUserId", // 顯示實際的用戶 ID
            color = Color.DarkGray, // 用深灰色區分
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp) // 在關卡列表前留出較大間距
        )


        // ** 【移除/隱藏】原本的 "選擇關卡" Text **
        // 因為截圖中顯示它已經在外層的 App Bar 中。如果您需要它，可以取消註釋。
        // Text("選擇關卡", style = MaterialTheme.typography.headlineMedium)
        // Spacer(modifier = Modifier.height(24.dp))

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
                            // 將 userProfile 與 mockUserId 存到 savedStateHandle，傳到 LevelSettingsScreen
                            navController.currentBackStackEntry
                                ?.savedStateHandle?.set("profile", userProfile)
                            navController.currentBackStackEntry
                                ?.savedStateHandle?.set("mockUserId", mockUserId)

                            navController.navigate("level_settings_screen")
                        }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // 確保 R.drawable 中的圖片名稱與 mode.imageName 匹配
                        // 例如，如果 mode.imageName 是 "mode1.png"，這裡會查找 R.drawable.mode1
                        val drawableId = context.resources.getIdentifier(
                            mode.imageName.substringBeforeLast("."),
                            "drawable",
                            context.packageName
                        )

                        // 處理找不到資源的情況，避免崩潰 (使用一個備用圖標或空白)
                        if (drawableId != 0) {
                            Image(
                                painter = painterResource(id = drawableId),
                                contentDescription = mode.name,
                                modifier = Modifier.fillMaxSize(0.7f)
                            )
                        } else {
                            Spacer(modifier = Modifier.fillMaxSize(0.7f)) // 找不到圖時留空
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(mode.name, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}