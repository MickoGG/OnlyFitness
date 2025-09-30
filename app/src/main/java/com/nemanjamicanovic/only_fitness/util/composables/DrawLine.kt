package com.nemanjamicanovic.only_fitness.util.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color


@Composable
fun DrawLine(
    color: Color = Color.LightGray,
    yOffset: Float = 0f,
    width: Float = 4f,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    Canvas(
        modifier = modifier
    ) {
        drawLine(
            color = color,
            start = Offset(0f, yOffset),
            end = Offset(size.width, yOffset),
            strokeWidth = width
        )
    }
}
