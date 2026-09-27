package com.hastaa.datausagemonitor.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SignalCellularAlt
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.hastaa.datausagemonitor.data.local.AppTheme
import com.hastaa.datausagemonitor.ui.components.metrics.CyberNeonNetworkMetricCard
import com.hastaa.datausagemonitor.ui.components.metrics.M3NetworkMetricCard
import com.hastaa.datausagemonitor.ui.theme.CyanNeon
import com.hastaa.datausagemonitor.ui.theme.LocalAppTheme
import com.hastaa.datausagemonitor.ui.theme.VioletNeon
import com.hastaa.datausagemonitor.util.ByteFormatter

@Composable
fun NetworkMetricsRow(
    mobileBytes: Long,
    wifiBytes: Long,
    modifier: Modifier = Modifier
) {
    val appTheme = LocalAppTheme.current
    val isCyberNeon = appTheme == AppTheme.CYBER_NEON

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        NetworkMetricCard(
            title = "Mobile\nData",
            bytes = mobileBytes,
            icon = Icons.Rounded.SignalCellularAlt,
            iconContainerColor = if (isCyberNeon) CyanNeon.copy(alpha = 0.15f) else MaterialTheme.colorScheme.primaryContainer,
            iconColor = if (isCyberNeon) CyanNeon else MaterialTheme.colorScheme.onPrimaryContainer,
            accentColor = if (isCyberNeon) CyanNeon else MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        )
        NetworkMetricCard(
            title = "Wi-Fi\nNetwork",
            bytes = wifiBytes,
            icon = Icons.Rounded.Wifi,
            iconContainerColor = if (isCyberNeon) VioletNeon.copy(alpha = 0.15f) else MaterialTheme.colorScheme.tertiaryContainer,
            iconColor = if (isCyberNeon) VioletNeon else MaterialTheme.colorScheme.onTertiaryContainer,
            accentColor = if (isCyberNeon) VioletNeon else MaterialTheme.colorScheme.tertiary,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        )
    }
}

@Composable
fun NetworkMetricCard(
    title: String,
    bytes: Long,
    icon: ImageVector,
    iconContainerColor: Color,
    iconColor: Color,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    val appTheme = LocalAppTheme.current
    val isCyberNeon = appTheme == AppTheme.CYBER_NEON
    val (value, unit) = ByteFormatter.formatBytesParts(bytes)

    if (isCyberNeon) {
        CyberNeonNetworkMetricCard(
            title = title,
            value = value,
            unit = unit,
            icon = icon,
            iconContainerColor = iconContainerColor,
            iconColor = iconColor,
            accentColor = accentColor,
            modifier = modifier
        )
    } else {
        M3NetworkMetricCard(
            title = title,
            value = value,
            unit = unit,
            icon = icon,
            iconContainerColor = iconContainerColor,
            iconColor = iconColor,
            accentColor = accentColor,
            modifier = modifier
        )
    }
}
