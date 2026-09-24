package com.hastaa.datausagemonitor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hastaa.datausagemonitor.ui.theme.DarkSurfaceBorder
import com.hastaa.datausagemonitor.ui.theme.DarkSurfaceElevated
import com.hastaa.datausagemonitor.ui.theme.DarkSurfaceGlass

/**
 * Reusable Glassmorphism Card for DataPulse.
 * Provides subtle dark translucency, soft border gradient, and rounded corners.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(22.dp),
    borderWidth: Dp = 1.dp,
    borderColor: Color = DarkSurfaceBorder,
    backgroundColor: Color = DarkSurfaceGlass,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        DarkSurfaceElevated.copy(alpha = 0.85f),
                        backgroundColor
                    )
                )
            )
            .border(
                width = borderWidth,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        borderColor.copy(alpha = 0.9f),
                        borderColor.copy(alpha = 0.4f)
                    )
                ),
                shape = shape
            ),
        content = content
    )
}
