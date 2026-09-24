package com.hastaa.datausagemonitor.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import com.hastaa.datausagemonitor.data.local.AppTheme
import com.hastaa.datausagemonitor.domain.model.AppDataUsage
import com.hastaa.datausagemonitor.ui.components.appitem.CyberNeonAppUsageItem
import com.hastaa.datausagemonitor.ui.components.appitem.M3AppUsageItem
import com.hastaa.datausagemonitor.ui.theme.LocalAppTheme

@Composable
fun AppUsageItem(
    app: AppDataUsage,
    maxUsageBytes: Long,
    modifier: Modifier = Modifier
) {
    val appTheme = LocalAppTheme.current
    val isCyberNeon = appTheme == AppTheme.CYBER_NEON

    val relativeRatio = if (maxUsageBytes > 0L) {
        (app.totalBytes.toFloat() / maxUsageBytes).coerceIn(0f, 1f)
    } else 0f

    val animatedRatio by animateFloatAsState(
        targetValue = relativeRatio,
        animationSpec = tween(400),
        label = "appProgress"
    )

    val iconImageBitmap = remember(app.iconBitmap) {
        app.iconBitmap?.asImageBitmap()
    }

    if (isCyberNeon) {
        CyberNeonAppUsageItem(
            app = app,
            iconImageBitmap = iconImageBitmap,
            animatedRatio = animatedRatio,
            modifier = modifier
        )
    } else {
        M3AppUsageItem(
            app = app,
            iconImageBitmap = iconImageBitmap,
            animatedRatio = animatedRatio,
            modifier = modifier
        )
    }
}
