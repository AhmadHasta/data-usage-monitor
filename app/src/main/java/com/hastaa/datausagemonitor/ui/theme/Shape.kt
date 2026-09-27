package com.hastaa.datausagemonitor.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Material Design 3 Expressive Shape scale.
 * Distinctive shape contrast establishes hierarchy across cards, buttons,
 * segmented controls, and icon containers.
 */
val Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

// Expressive squircle shapes for icons and prominent hero elements
val SquircleSmall = RoundedCornerShape(10.dp)
val SquircleMedium = RoundedCornerShape(14.dp)
val SquircleLarge = RoundedCornerShape(16.dp)
val SquircleHero = RoundedCornerShape(28.dp)
val PillShape = RoundedCornerShape(9999.dp)
