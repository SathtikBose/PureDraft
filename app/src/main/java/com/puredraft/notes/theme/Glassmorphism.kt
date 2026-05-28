package com.puredraft.notes.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.Dp

fun Modifier.glassmorphism(cornerRadius: Dp = 16.dp): Modifier {
    return this
        .shadow(
            elevation = 12.dp,
            shape = RoundedCornerShape(cornerRadius),
            ambientColor = Color.Black.copy(alpha = 0.8f),
            spotColor = Color.Black.copy(alpha = 0.8f)
        )
        .clip(RoundedCornerShape(cornerRadius))
        .background(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.15f),
                    Color.White.copy(alpha = 0.05f)
                )
            )
        )
        .border(
            width = 1.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.3f),
                    Color.White.copy(alpha = 0.05f)
                )
            ),
            shape = RoundedCornerShape(cornerRadius)
        )
}
