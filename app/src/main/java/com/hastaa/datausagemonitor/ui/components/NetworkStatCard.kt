package com.hastaa.datausagemonitor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SignalCellularAlt
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hastaa.datausagemonitor.ui.theme.AccentPrimary
import com.hastaa.datausagemonitor.ui.theme.AccentSecondary
import com.hastaa.datausagemonitor.ui.theme.TextPrimary
import com.hastaa.datausagemonitor.ui.theme.TextSecondary
import com.hastaa.datausagemonitor.ui.theme.TextTertiary
import com.hastaa.datausagemonitor.util.ByteFormatter

/**
 * Compact Network Stat Card (Mobile or Wi-Fi).
 */
@Composable
fun NetworkStatCard(
    title: String,
    bytes: Long,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    periodSubtext: String = "Today"
) {
    val (value, unit) = ByteFormatter.formatBytesParts(bytes)

    GlassCard(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Icon & Title Header
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = accentColor,
                        modifier = Modifier.size(17.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextSecondary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Value + Unit
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineLarge.copy(fontSize = 26.sp),
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = unit,
                    style = MaterialTheme.typography.titleMedium,
                    color = accentColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Subtext (e.g. "Today")
            Text(
                text = periodSubtext,
                style = MaterialTheme.typography.labelSmall,
                color = TextTertiary
            )
        }
    }
}

/**
 * Side-by-side row displaying Mobile and Wi-Fi cards.
 */
@Composable
fun NetworkStatsRow(
    mobileBytes: Long,
    wifiBytes: Long,
    modifier: Modifier = Modifier,
    periodSubtext: String = "Today"
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        NetworkStatCard(
            title = "Mobile",
            bytes = mobileBytes,
            icon = Icons.Rounded.SignalCellularAlt,
            accentColor = AccentPrimary,
            modifier = Modifier.weight(1f),
            periodSubtext = periodSubtext
        )
        NetworkStatCard(
            title = "Wi-Fi",
            bytes = wifiBytes,
            icon = Icons.Rounded.Wifi,
            accentColor = AccentSecondary,
            modifier = Modifier.weight(1f),
            periodSubtext = periodSubtext
        )
    }
}
