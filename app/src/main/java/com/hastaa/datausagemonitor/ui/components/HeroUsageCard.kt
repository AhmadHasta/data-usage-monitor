package com.hastaa.datausagemonitor.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.hastaa.datausagemonitor.data.local.AppTheme
import com.hastaa.datausagemonitor.domain.model.UsagePeriod
import com.hastaa.datausagemonitor.ui.components.hero.CyberNeonHeroUsageCard
import com.hastaa.datausagemonitor.ui.components.hero.M3HeroUsageCard
import com.hastaa.datausagemonitor.ui.theme.LocalAppTheme
import com.hastaa.datausagemonitor.util.ByteFormatter

@Composable
fun HeroUsageCard(
    totalBytes: Long,
    mobileBytes: Long,
    wifiBytes: Long,
    period: UsagePeriod,
    modifier: Modifier = Modifier
) {
    val appTheme = LocalAppTheme.current
    val isCyberNeon = appTheme == AppTheme.CYBER_NEON

    val (value, unit) = ByteFormatter.formatBytesParts(totalBytes)

    val mobileRatio = if (totalBytes > 0) (mobileBytes.toFloat() / totalBytes).coerceIn(0f, 1f) else 0f
    val wifiRatio = if (totalBytes > 0) (wifiBytes.toFloat() / totalBytes).coerceIn(0f, 1f) else 0f

    val animatedMobileRatio by animateFloatAsState(
        targetValue = mobileRatio,
        animationSpec = tween(600),
        label = "mobileRatio"
    )
    val animatedWifiRatio by animateFloatAsState(
        targetValue = wifiRatio,
        animationSpec = tween(600),
        label = "wifiRatio"
    )

    if (isCyberNeon) {
        CyberNeonHeroUsageCard(
            value = value,
            unit = unit,
            period = period,
            mobileRatio = mobileRatio,
            wifiRatio = wifiRatio,
            animatedMobileRatio = animatedMobileRatio,
            animatedWifiRatio = animatedWifiRatio,
            modifier = modifier
        )
    } else {
        M3HeroUsageCard(
            value = value,
            unit = unit,
            period = period,
            mobileRatio = mobileRatio,
            wifiRatio = wifiRatio,
            animatedMobileRatio = animatedMobileRatio,
            animatedWifiRatio = animatedWifiRatio,
            modifier = modifier
        )
    }
}
