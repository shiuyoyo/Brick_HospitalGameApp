package com.example.brick_hospitalgameapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.brick_hospitalgameapp.models.UserProfile
import com.example.brick_hospitalgameapp.network.SupabaseAuth
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavHostController, userProfile: UserProfile?) {

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
        ) {
            Text("Brick Hospital Game", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(24.dp))

            // Username
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Password
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Supabase 登入按鈕
            Button(
                onClick = {
                    scope.launch {
                        isLoading = true
                        errorMessage = ""

                        try {
                            println("🔍 Supabase 查詢 Username: $username")
                            val profile = SupabaseAuth.getUserProfileByUsername(username.trim())
                            if (profile == null) {
                                println("❌ Supabase 查詢異常: $profile")
                                errorMessage = "找不到此帳號"
                            } else {
                                println("✅ Supabase 回傳 UserProfile: $profile")
                                val currentUserId = profile.id ?: "mock_user"
                                navController.navigate("mode_select/$currentUserId") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            errorMessage = "登入異常: ${e.message}"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isLoading) "登入中..." else "登入")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 訪客登入
            Button(
                onClick = {
                    val mockUserId = "mock_user"
                    navController.navigate("mode_select/$mockUserId") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text("不登入直接遊玩")
            }

            if (errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(errorMessage, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
