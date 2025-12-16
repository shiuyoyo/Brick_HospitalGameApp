package com.example.brick_hospitalgameapp.ui.shapes

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape

// 六邊形
val GenericShapeHexagon = GenericShape { size, _ ->
    val width = size.width
    val height = size.height
    moveTo(width * 0.5f, 0f)
    lineTo(width, height * 0.25f)
    lineTo(width, height * 0.75f)
    lineTo(width * 0.5f, height)
    lineTo(0f, height * 0.75f)
    lineTo(0f, height * 0.25f)
    close()
}

// 長矩形
val GenericShapeRectangle = GenericShape { size, _ ->
    addRect(androidx.compose.ui.geometry.Rect(0f, 0f, size.width, size.height))
}

// 正三角形
val GenericShapeTriangle = GenericShape { size, _ ->
    moveTo(size.width / 2f, 0f)
    lineTo(size.width, size.height)
    lineTo(0f, size.height)
    close()
}

// 正方形
val GenericShapeSquare = GenericShape { size, _ ->
    addRect(androidx.compose.ui.geometry.Rect(0f, 0f, size.width, size.height))
}

// 圓形
val GenericShapeCircle = CircleShape

// Default 遊戲形狀列表
val DefaultShapesList = listOf(
    GenericShapeHexagon,
    GenericShapeRectangle,
    GenericShapeTriangle,
    GenericShapeSquare,
    GenericShapeCircle
)
