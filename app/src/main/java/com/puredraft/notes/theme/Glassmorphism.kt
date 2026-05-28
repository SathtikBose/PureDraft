package com.puredraft.notes.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

fun Modifier.glassmorphism(): Modifier {
    return this
        .clip(RoundedCornerShape(16.dp))
        .background(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.2f),
                    Color.White.copy(alpha = 0.05f)
                )
            )
        )
        .border(
            width = 1.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.5f),
                    Color.White.copy(alpha = 0.1f)
                )
            ),
            shape = RoundedCornerShape(16.dp)
        )
}
