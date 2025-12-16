package com.example.brick_hospitalgameapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.brick_hospitalgameapp.models.UserProfile

@Composable
fun LevelSettingsShapesScreen(
    navController: NavController,
    levelName: String,
    mockUserId: UserProfile?
) {
    val context = LocalContext.current
    
    // 設定狀態
    var gameMode by remember { mutableStateOf("single") } // "single" 或 "multi"
    var colorMode by remember { mutableStateOf("sequence") } // "sequence", "random", "fixed"
    var totalTimeSeconds by remember { mutableStateOf(60) }
    
    Box(modifier = Modifier.fillMaxSize()) {
        // 背景
        Image(
            painter = painterResource(
                id = context.resources.getIdentifier("game_bg", "drawable", context.packageName)
            ),
            contentDescription = "背景",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "形狀遊戲設定",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = levelName,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // 遊戲模式選擇
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "遊戲模式",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.Black
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // 單色模式
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = gameMode == "single",
                                onClick = { gameMode = "single" }
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = gameMode == "single",
                            onClick = { gameMode = "single" }
                        )
                        Text(
                            text = "單色模式",
                            color = Color.Black,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                    
                    // 多色模式
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = gameMode == "multi",
                                onClick = { gameMode = "multi" }
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = gameMode == "multi",
                            onClick = { gameMode = "multi" }
                        )
                        Text(
                            text = "多色模式",
                            color = Color.Black,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
            
            // 多色模式的顏色變化設定
            if (gameMode == "multi") {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "顏色變化模式",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.Black
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // 順序模式
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = colorMode == "sequence",
                                    onClick = { colorMode = "sequence" }
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = colorMode == "sequence",
                                onClick = { colorMode = "sequence" }
                            )
                            Text(
                                text = "順序變化",
                                color = Color.Black,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                        
                        // 隨機模式
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = colorMode == "random",
                                    onClick = { colorMode = "random" }
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = colorMode == "random",
                                onClick = { colorMode = "random" }
                            )
                            Text(
                                text = "隨機變化",
                                color = Color.Black,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }
            
            // 時間設定
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "遊戲時間: ${totalTimeSeconds}秒",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.Black
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf(30, 60, 90, 120).forEach { time ->
                            Button(
                                onClick = { totalTimeSeconds = time },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (totalTimeSeconds == time) Color.Blue else Color.Gray
                                )
                            ) {
                                Text("${time}s", color = Color.White)
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // 開始遊戲按鈕
            Button(
                onClick = {
                    val route = if (gameMode == "single") {
                        "game_shapes_single/$levelName/$mockUserId/$totalTimeSeconds"
                    } else {
                        "game_shapes_multi/$levelName/$mockUserId/$colorMode"
                    }
                    navController.navigate(route)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
            ) {
                Text(
                    text = "開始遊戲",
                    color = Color.White,
                    fontSize = 20.sp
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // 返回按鈕
            Button(
                onClick = {
                    navController.navigate("level_selection/$mockUserId") {
                        popUpTo("level_settings_shapes/$levelName/$mockUserId") { 
                            inclusive = true 
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) {
                Text("返回關卡選擇", color = Color.White)
            }
        }
    }
}
