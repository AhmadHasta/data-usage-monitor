package com.hastaa.datausagemonitor.ui.screen.settings.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hastaa.datausagemonitor.domain.model.TileConfig
import com.hastaa.datausagemonitor.domain.model.TileContentStyle
import com.hastaa.datausagemonitor.ui.screen.settings.components.SectionHeader
import com.hastaa.datausagemonitor.ui.screen.settings.components.SeekbarSettingItem
import kotlin.math.roundToInt

@Composable
fun TileSizingSection(
    config: TileConfig,
    onResetStyleSizing: (TileContentStyle) -> Unit,
    onMetricIconSizeChange: (Int) -> Unit,
    onIconStrokeWidthChange: (Int) -> Unit,
    onMetricValueTextSizeChange: (Int) -> Unit,
    onMetricUnitTextSizeChange: (Int) -> Unit,
    onMetricSpacingChange: (Int) -> Unit,
    onRingDiameterChange: (Int) -> Unit,
    onRingStrokeWidthChange: (Int) -> Unit,
    onRingSweepAngleChange: (Int) -> Unit,
    onProgressIconSizeChange: (Int) -> Unit,
    onProgressRingDiameterChange: (Int) -> Unit,
    onProgressRingStrokeChange: (Int) -> Unit,
    onIconOnlySizeChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        SectionHeader(
            title = "Size & In-Tile Layout",
            subtitle = "Adjust icon size, text, and layout for the selected style",
            icon = Icons.Rounded.Tune
        )
        Spacer(modifier = Modifier.height(10.dp))
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Style: ${config.contentStyle.label}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    TextButton(
                        onClick = { onResetStyleSizing(config.contentStyle) },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Reset",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }

                when (config.contentStyle) {
                    TileContentStyle.METRIC_WITH_ICON -> {
                        SeekbarSettingItem(
                            label = "Network Icon Size",
                            value = config.metricIconSizeDp.toFloat(),
                            valueDisplay = "${config.metricIconSizeDp} dp",
                            valueRange = 12f..100f,
                            onValueChange = { onMetricIconSizeChange(it.roundToInt()) }
                        )

                        SeekbarSettingItem(
                            label = "Icon Line Thickness (Stroke)",
                            value = config.iconStrokeWidthDp.toFloat(),
                            valueDisplay = "${config.iconStrokeWidthDp} dp",
                            valueRange = 1f..6f,
                            onValueChange = { onIconStrokeWidthChange(it.roundToInt()) }
                        )

                        SeekbarSettingItem(
                            label = "Number Text Size",
                            value = config.metricValueTextSizeSp.toFloat(),
                            valueDisplay = "${config.metricValueTextSizeSp} sp",
                            valueRange = 14f..30f,
                            onValueChange = { onMetricValueTextSizeChange(it.roundToInt()) }
                        )

                        SeekbarSettingItem(
                            label = "Unit Text Size (GB/MB)",
                            value = config.metricUnitTextSizeSp.toFloat(),
                            valueDisplay = "${config.metricUnitTextSizeSp} sp",
                            valueRange = 8f..18f,
                            onValueChange = { onMetricUnitTextSizeChange(it.roundToInt()) }
                        )

                        SeekbarSettingItem(
                            label = "Element Spacing",
                            value = config.metricSpacingDp.toFloat(),
                            valueDisplay = "${config.metricSpacingDp} dp",
                            valueRange = -2f..10f,
                            onValueChange = { onMetricSpacingChange(it.roundToInt()) }
                        )
                    }
                    TileContentStyle.PROGRESS_RING -> {
                        SeekbarSettingItem(
                            label = "Ring Circle Diameter",
                            value = config.ringDiameterDp.toFloat(),
                            valueDisplay = "${config.ringDiameterDp} dp",
                            valueRange = 36f..100f,
                            onValueChange = { onRingDiameterChange(it.roundToInt()) }
                        )

                        SeekbarSettingItem(
                            label = "Arc Line Thickness (Stroke)",
                            value = config.ringStrokeWidthDp.toFloat(),
                            valueDisplay = "${config.ringStrokeWidthDp} dp",
                            valueRange = 4f..14f,
                            onValueChange = { onRingStrokeWidthChange(it.roundToInt()) }
                        )

                        SeekbarSettingItem(
                            label = "Maximum Arc Sweep Angle",
                            value = config.ringSweepAngleDeg.toFloat(),
                            valueDisplay = "${config.ringSweepAngleDeg}°",
                            valueRange = 180f..360f,
                            step = 10f,
                            onValueChange = { onRingSweepAngleChange(it.roundToInt()) }
                        )
                    }
                    TileContentStyle.PROGRESS_WITH_ICON -> {
                        SeekbarSettingItem(
                            label = "Center Icon Size",
                            value = config.progressIconSizeDp.toFloat(),
                            valueDisplay = "${config.progressIconSizeDp} dp",
                            valueRange = 14f..100f,
                            onValueChange = { onProgressIconSizeChange(it.roundToInt()) }
                        )

                        SeekbarSettingItem(
                            label = "Icon Line Thickness (Stroke)",
                            value = config.iconStrokeWidthDp.toFloat(),
                            valueDisplay = "${config.iconStrokeWidthDp} dp",
                            valueRange = 1f..6f,
                            onValueChange = { onIconStrokeWidthChange(it.roundToInt()) }
                        )

                        SeekbarSettingItem(
                            label = "Outer Ring Diameter",
                            value = config.progressRingDiameterDp.toFloat(),
                            valueDisplay = "${config.progressRingDiameterDp} dp",
                            valueRange = 40f..100f,
                            onValueChange = { onProgressRingDiameterChange(it.roundToInt()) }
                        )

                        SeekbarSettingItem(
                            label = "Ring Line Thickness (Stroke)",
                            value = config.progressRingStrokeDp.toFloat(),
                            valueDisplay = "${config.progressRingStrokeDp} dp",
                            valueRange = 2f..10f,
                            onValueChange = { onProgressRingStrokeChange(it.roundToInt()) }
                        )
                    }
                    TileContentStyle.ICON_ONLY -> {
                        SeekbarSettingItem(
                            label = "Network Icon Size",
                            value = config.iconOnlySizeDp.toFloat(),
                            valueDisplay = "${config.iconOnlySizeDp} dp",
                            valueRange = 24f..100f,
                            onValueChange = { onIconOnlySizeChange(it.roundToInt()) }
                        )

                        SeekbarSettingItem(
                            label = "Icon Line Thickness (Stroke)",
                            value = config.iconStrokeWidthDp.toFloat(),
                            valueDisplay = "${config.iconStrokeWidthDp} dp",
                            valueRange = 1f..6f,
                            onValueChange = { onIconStrokeWidthChange(it.roundToInt()) }
                        )
                    }
                }
            }
        }
    }
}
