package com.example.brick_hospitalgameapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.brick_hospitalgameapp.models.UserProfile

@Composable
fun ProfileScreen(navController: NavController, userId: UserProfile?) {
    val profile = navController.previousBackStackEntry
        ?.savedStateHandle
        ?.get<UserProfile>("profile")

    if (profile == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("無法取得使用者資料", color = Color.Red)
        }
        return
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        Text("Welcome, ${profile.full_name}", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Username: ${profile.username}")
        Text("Email: ${profile.email}")
        profile.phone?.let { Text("Phone: $it") }

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { navController.navigate("mode_select/${profile.user_id}") },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5))
        ) {
            Text("選擇動作", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.popBackStack() }) {
            Text("返回")
        }
    }
}
