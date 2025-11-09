package com.example.brick_hospitalgameapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.brick_hospitalgameapp.models.UserProfile

@Composable
fun GameScreen(
    navController: NavController,
    levelName: String,
    userProfile: UserProfile?,
    mockUserId: String?
) {
    val currentUserId = userProfile?.id ?: mockUserId ?: "unknown"
    var score by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("模式: $levelName")
        Spacer(modifier = Modifier.height(16.dp))
        Text("玩家ID: $currentUserId")
        Spacer(modifier = Modifier.height(16.dp))
        Text("Score: $score")
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { score += 10 }) {
            Text("增加分數 (+10)")
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { navController.popBackStack() }) {
            Text("返回選擇模式")
        }
    }
}
