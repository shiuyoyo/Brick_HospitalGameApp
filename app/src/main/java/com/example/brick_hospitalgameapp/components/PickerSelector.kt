package com.example.brick_hospitalgameapp.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun <T> PickerSelector(
    label: String,
    options: List<T>,
    selected: T,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(label, color = Color(0xFF7B8FA6))
        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .width(180.dp)
                .clickable { showDialog = true }
                .background(Color.White, RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(selected.toString(), color = Color.Black)
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("選擇 $label") },
                text = {
                    Column {
                        options.forEach { option ->
                            Text(
                                text = option.toString(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onSelect(option)
                                        showDialog = false
                                    }
                                    .padding(8.dp),
                                color = Color.Black
                            )
                        }
                    }
                },
                confirmButton = {}
            )
        }
    }
}
