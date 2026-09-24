package com.hastaa.datausagemonitor.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = AccentPrimary,
    onPrimary = DarkBackground,
    primaryContainer = AccentPrimarySubtle,
    onPrimaryContainer = AccentPrimary,
    secondary = AccentSecondary,
    onSecondary = DarkBackground,
    secondaryContainer = AccentSecondarySubtle,
    onSecondaryContainer = AccentSecondary,
    tertiary = AccentSuccess,
    onTertiary = DarkBackground,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = DarkSurfaceBorder,
    error = AccentError,
    onError = DarkBackground
)

private val LightColorScheme = lightColorScheme(
    primary = AccentPrimary,
    onPrimary = LightSurface,
    primaryContainer = AccentPrimarySubtle,
    onPrimaryContainer = AccentPrimary,
    secondary = AccentSecondary,
    onSecondary = LightSurface,
    secondaryContainer = AccentSecondarySubtle,
    onSecondaryContainer = AccentSecondary,
    tertiary = AccentSuccess,
    onTertiary = LightSurface,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceElevated,
    onSurfaceVariant = LightTextSecondary,
    outline = LightSurfaceBorder,
    error = AccentError,
    onError = LightSurface
)

@Composable
fun DataUsageMonitorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // For DataPulse Phase 0, we prioritize dark theme by default, or support system theme
    val colorScheme = if (darkTheme) DarkColorScheme else DarkColorScheme // Dark theme is central to DataPulse identity
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = false
            insetsController.isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
