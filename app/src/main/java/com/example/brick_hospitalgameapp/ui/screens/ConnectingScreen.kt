package com.example.brick_hospitalgameapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.brick_hospitalgameapp.models.UserProfile
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ConnectingScreen(
    navController: NavController,
    levelName: String,
    userProfile: UserProfile?,
    mockUserId: String?
) {
    val currentUserId = userProfile?.id ?: mockUserId ?: "unknown"
    var connectingText by remember { mutableStateOf("連線中...") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            delay(2000)
            connectingText = "連線成功!"
            delay(500)
            navController.currentBackStackEntry?.savedStateHandle?.set("profile", userProfile)
            navController.currentBackStackEntry?.savedStateHandle?.set("mockUserId", mockUserId)
            navController.navigate("game_screen/$levelName")
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text(connectingText)
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = {
                navController.currentBackStackEntry?.savedStateHandle?.set("profile", userProfile)
                navController.currentBackStackEntry?.savedStateHandle?.set("mockUserId", mockUserId)
                navController.navigate("game_screen/$levelName")
            }) {
                Text("跳過直接遊戲")
            }
        }
    }
}
