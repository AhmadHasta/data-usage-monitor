package com.hastaa.datausagemonitor.ui.screen.settings.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DataUsage
import androidx.compose.material.icons.rounded.SignalCellularAlt
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hastaa.datausagemonitor.domain.model.TileIconChoice

@Composable
fun IconChoiceGridItem(
    choice: TileIconChoice,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.surfaceContainerHighest,
        label = "iconChoiceBg"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
        else MaterialTheme.colorScheme.onSurface,
        label = "iconChoiceContent"
    )

    Surface(
        onClick = onSelect,
        shape = RoundedCornerShape(14.dp),
        color = containerColor,
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(14.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = when (choice) {
                    TileIconChoice.AUTO -> Icons.Rounded.Speed
                    TileIconChoice.WIFI -> Icons.Rounded.Wifi
                    TileIconChoice.CELLULAR -> Icons.Rounded.SignalCellularAlt
                    TileIconChoice.DATA_USAGE -> Icons.Rounded.DataUsage
                },
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = when (choice) {
                    TileIconChoice.AUTO -> "Auto"
                    TileIconChoice.WIFI -> "Wi-Fi"
                    TileIconChoice.CELLULAR -> "Cellular"
                    TileIconChoice.DATA_USAGE -> "Data Usage"
                },
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = contentColor,
                maxLines = 1,
                softWrap = false
            )
        }
    }
}
