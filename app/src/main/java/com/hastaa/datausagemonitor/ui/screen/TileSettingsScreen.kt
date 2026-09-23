package com.hastaa.datausagemonitor.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.DataUsage
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.DonutLarge
import androidx.compose.material.icons.rounded.Numbers
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.SignalCellularAlt
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.ViewHeadline
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hastaa.datausagemonitor.data.local.TileConfig
import com.hastaa.datausagemonitor.data.local.TileContentStyle
import com.hastaa.datausagemonitor.data.local.TileIconChoice
import com.hastaa.datausagemonitor.data.local.TileTextLayout
import com.hastaa.datausagemonitor.domain.model.UsagePeriod
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TileSettingsScreen(
    onBack: () -> Unit,
    viewModel: TileSettingsViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val config = state.config

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Customize Tile",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Configure tile content & text format",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item(key = "section_content_style") {
                SectionHeader(
                    title = "Tile Circle Content",
                    subtitle = "Select elements displayed inside the Quick Settings tile circle",
                    icon = Icons.Rounded.Tune
                )
                Spacer(modifier = Modifier.height(10.dp))
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    TileContentStyle.values().forEach { style ->
                        ContentStyleChoiceCard(
                            style = style,
                            isSelected = config.contentStyle == style,
                            onSelect = { viewModel.setContentStyle(style) }
                        )
                    }
                }
            }

            item(key = "section_layout_sizing") {
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
                                onClick = { viewModel.resetStyleSizing(config.contentStyle) },
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
                                    onValueChange = { viewModel.setMetricIconSize(it.roundToInt()) }
                                )

                                SeekbarSettingItem(
                                    label = "Icon Line Thickness (Stroke)",
                                    value = config.iconStrokeWidthDp.toFloat(),
                                    valueDisplay = "${config.iconStrokeWidthDp} dp",
                                    valueRange = 1f..6f,
                                    onValueChange = { viewModel.setIconStrokeWidth(it.roundToInt()) }
                                )

                                SeekbarSettingItem(
                                    label = "Number Text Size",
                                    value = config.metricValueTextSizeSp.toFloat(),
                                    valueDisplay = "${config.metricValueTextSizeSp} sp",
                                    valueRange = 14f..30f,
                                    onValueChange = { viewModel.setMetricValueTextSize(it.roundToInt()) }
                                )

                                SeekbarSettingItem(
                                    label = "Unit Text Size (GB/MB)",
                                    value = config.metricUnitTextSizeSp.toFloat(),
                                    valueDisplay = "${config.metricUnitTextSizeSp} sp",
                                    valueRange = 8f..18f,
                                    onValueChange = { viewModel.setMetricUnitTextSize(it.roundToInt()) }
                                )

                                SeekbarSettingItem(
                                    label = "Element Spacing",
                                    value = config.metricSpacingDp.toFloat(),
                                    valueDisplay = "${config.metricSpacingDp} dp",
                                    valueRange = -2f..10f,
                                    onValueChange = { viewModel.setMetricSpacing(it.roundToInt()) }
                                )
                            }
                            TileContentStyle.PROGRESS_RING -> {
                                SeekbarSettingItem(
                                    label = "Ring Circle Diameter",
                                    value = config.ringDiameterDp.toFloat(),
                                    valueDisplay = "${config.ringDiameterDp} dp",
                                    valueRange = 36f..100f,
                                    onValueChange = { viewModel.setRingDiameter(it.roundToInt()) }
                                )

                                SeekbarSettingItem(
                                    label = "Arc Line Thickness (Stroke)",
                                    value = config.ringStrokeWidthDp.toFloat(),
                                    valueDisplay = "${config.ringStrokeWidthDp} dp",
                                    valueRange = 4f..14f,
                                    onValueChange = { viewModel.setRingStrokeWidth(it.roundToInt()) }
                                )

                                SeekbarSettingItem(
                                    label = "Maximum Arc Sweep Angle",
                                    value = config.ringSweepAngleDeg.toFloat(),
                                    valueDisplay = "${config.ringSweepAngleDeg}°",
                                    valueRange = 180f..360f,
                                    step = 10f,
                                    onValueChange = { viewModel.setRingSweepAngle(it.roundToInt()) }
                                )
                            }
                            TileContentStyle.PROGRESS_WITH_ICON -> {
                                SeekbarSettingItem(
                                    label = "Center Icon Size",
                                    value = config.progressIconSizeDp.toFloat(),
                                    valueDisplay = "${config.progressIconSizeDp} dp",
                                    valueRange = 14f..100f,
                                    onValueChange = { viewModel.setProgressIconSize(it.roundToInt()) }
                                )

                                SeekbarSettingItem(
                                    label = "Icon Line Thickness (Stroke)",
                                    value = config.iconStrokeWidthDp.toFloat(),
                                    valueDisplay = "${config.iconStrokeWidthDp} dp",
                                    valueRange = 1f..6f,
                                    onValueChange = { viewModel.setIconStrokeWidth(it.roundToInt()) }
                                )

                                SeekbarSettingItem(
                                    label = "Outer Ring Diameter",
                                    value = config.progressRingDiameterDp.toFloat(),
                                    valueDisplay = "${config.progressRingDiameterDp} dp",
                                    valueRange = 40f..100f,
                                    onValueChange = { viewModel.setProgressRingDiameter(it.roundToInt()) }
                                )

                                SeekbarSettingItem(
                                    label = "Ring Line Thickness (Stroke)",
                                    value = config.progressRingStrokeDp.toFloat(),
                                    valueDisplay = "${config.progressRingStrokeDp} dp",
                                    valueRange = 2f..10f,
                                    onValueChange = { viewModel.setProgressRingStroke(it.roundToInt()) }
                                )
                            }
                            TileContentStyle.ICON_ONLY -> {
                                SeekbarSettingItem(
                                    label = "Network Icon Size",
                                    value = config.iconOnlySizeDp.toFloat(),
                                    valueDisplay = "${config.iconOnlySizeDp} dp",
                                    valueRange = 24f..100f,
                                    onValueChange = { viewModel.setIconOnlySize(it.roundToInt()) }
                                )

                                SeekbarSettingItem(
                                    label = "Icon Line Thickness (Stroke)",
                                    value = config.iconStrokeWidthDp.toFloat(),
                                    valueDisplay = "${config.iconStrokeWidthDp} dp",
                                    valueRange = 1f..6f,
                                    onValueChange = { viewModel.setIconStrokeWidth(it.roundToInt()) }
                                )
                            }
                        }
                    }
                }
            }

            item(key = "section_text_layout") {
                SectionHeader(
                    title = "Tile Text Layout",
                    subtitle = "Text format below the Quick Settings toggle button",
                    icon = Icons.Rounded.TextFields
                )
                Spacer(modifier = Modifier.height(10.dp))
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    TileTextLayout.values().forEach { layout ->
                        TextLayoutChoiceCard(
                            layout = layout,
                            isSelected = config.textLayout == layout,
                            onSelect = { viewModel.setTextLayout(layout) }
                        )
                    }
                }
            }

            item(key = "section_icon_choice") {
                SectionHeader(
                    title = "Network Icon Choice",
                    subtitle = "Icon symbol used on the tile",
                    icon = Icons.Rounded.Wifi
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
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            IconChoiceGridItem(
                                choice = TileIconChoice.AUTO,
                                isSelected = config.iconChoice == TileIconChoice.AUTO,
                                onSelect = { viewModel.setIconChoice(TileIconChoice.AUTO) },
                                modifier = Modifier.weight(1f)
                            )
                            IconChoiceGridItem(
                                choice = TileIconChoice.WIFI,
                                isSelected = config.iconChoice == TileIconChoice.WIFI,
                                onSelect = { viewModel.setIconChoice(TileIconChoice.WIFI) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            IconChoiceGridItem(
                                choice = TileIconChoice.CELLULAR,
                                isSelected = config.iconChoice == TileIconChoice.CELLULAR,
                                onSelect = { viewModel.setIconChoice(TileIconChoice.CELLULAR) },
                                modifier = Modifier.weight(1f)
                            )
                            IconChoiceGridItem(
                                choice = TileIconChoice.DATA_USAGE,
                                isSelected = config.iconChoice == TileIconChoice.DATA_USAGE,
                                onSelect = { viewModel.setIconChoice(TileIconChoice.DATA_USAGE) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            item(key = "section_period") {
                SectionHeader(
                    title = "Data Usage Period",
                    subtitle = "Time range calculated in the Quick Settings tile",
                    icon = Icons.Rounded.DateRange
                )
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            UsagePeriod.TODAY,
                            UsagePeriod.THIS_WEEK,
                            UsagePeriod.THIS_MONTH
                        ).forEach { period ->
                            PeriodChoiceItem(
                                label = period.label,
                                isSelected = config.period == period,
                                onSelect = { viewModel.setPeriod(period) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            item(key = "section_quota_limit") {
                AnimatedVisibility(
                    visible = config.contentStyle == TileContentStyle.PROGRESS_RING ||
                            config.contentStyle == TileContentStyle.PROGRESS_WITH_ICON
                ) {
                    Column {
                        SectionHeader(
                            title = "Quota Target (Ring Base)",
                            subtitle = "Quota threshold to calculate progress ring sweep",
                            icon = Icons.Rounded.DonutLarge
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Quota Target Limit",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Full capacity target (100%)",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        modifier = Modifier.widthIn(min = 72.dp)
                                    ) {
                                        Text(
                                            text = "${config.quotaLimitGigaBytes} GB",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                            maxLines = 1,
                                            softWrap = false
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    listOf(5, 10, 20, 30, 50).forEach { presetGb ->
                                        val isCurrent = config.quotaLimitGigaBytes == presetGb
                                        Surface(
                                            onClick = { viewModel.setQuotaLimit(presetGb) },
                                            shape = RoundedCornerShape(10.dp),
                                            color = if (isCurrent) MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.surfaceContainerHighest,
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(36.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                        ) {
                                            Box(
                                                modifier = Modifier.fillMaxSize(),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "$presetGb GB",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                                    color = if (isCurrent) MaterialTheme.colorScheme.onPrimary
                                                    else MaterialTheme.colorScheme.onSurface,
                                                    maxLines = 1,
                                                    softWrap = false
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    FilledTonalIconButton(
                                        onClick = {
                                            viewModel.setQuotaLimit((config.quotaLimitGigaBytes - 1).coerceIn(1, 50))
                                        },
                                        enabled = config.quotaLimitGigaBytes > 1,
                                        modifier = Modifier.size(36.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                                            contentColor = MaterialTheme.colorScheme.onSurface,
                                            disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.38f),
                                            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                                        )
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Remove,
                                            contentDescription = "Decrease Quota",
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    Slider(
                                        value = config.quotaLimitGigaBytes.toFloat(),
                                        onValueChange = { viewModel.setQuotaLimit(it.roundToInt().coerceIn(1, 50)) },
                                        valueRange = 1f..50f,
                                        modifier = Modifier.weight(1f),
                                        colors = SliderDefaults.colors(
                                            thumbColor = MaterialTheme.colorScheme.primary,
                                            activeTrackColor = MaterialTheme.colorScheme.primary,
                                            inactiveTrackColor = MaterialTheme.colorScheme.surfaceContainerHighest
                                        )
                                    )

                                    FilledTonalIconButton(
                                        onClick = {
                                            viewModel.setQuotaLimit((config.quotaLimitGigaBytes + 1).coerceIn(1, 50))
                                        },
                                        enabled = config.quotaLimitGigaBytes < 50,
                                        modifier = Modifier.size(36.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                                            contentColor = MaterialTheme.colorScheme.onSurface,
                                            disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.38f),
                                            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                                        )
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Add,
                                            contentDescription = "Increase Quota",
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "1 GB",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "50 GB",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item(key = "bottom_space") {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun ContentStyleChoiceCard(
    style: TileContentStyle,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val containerColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
        else MaterialTheme.colorScheme.surfaceContainerLow,
        label = "styleBg"
    )

    Card(
        onClick = onSelect,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceContainerHighest
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (style) {
                        TileContentStyle.METRIC_WITH_ICON -> Icons.Rounded.Numbers
                        TileContentStyle.PROGRESS_RING -> Icons.Rounded.DonutLarge
                        TileContentStyle.PROGRESS_WITH_ICON -> Icons.Rounded.Speed
                        TileContentStyle.ICON_ONLY -> Icons.Rounded.Wifi
                    },
                    contentDescription = null,
                    tint = if (isSelected) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = style.label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = style.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            RadioButton(
                selected = isSelected,
                onClick = onSelect,
                colors = RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

@Composable
private fun TextLayoutChoiceCard(
    layout: TileTextLayout,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val containerColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
        else MaterialTheme.colorScheme.surfaceContainerLow,
        label = "layoutBg"
    )

    Card(
        onClick = onSelect,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceContainerHighest
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (layout) {
                        TileTextLayout.SINGLE_LINE -> Icons.Rounded.ViewHeadline
                        TileTextLayout.DUAL_LINE_NETWORK_FIRST -> Icons.Rounded.TextFields
                        TileTextLayout.DUAL_LINE_METRIC_FIRST -> Icons.Rounded.Numbers
                    },
                    contentDescription = null,
                    tint = if (isSelected) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = layout.label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Preview: ",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (layout.previewSecond.isNotBlank())
                            "${layout.previewFirst} / ${layout.previewSecond}"
                        else layout.previewFirst,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            RadioButton(
                selected = isSelected,
                onClick = onSelect,
                colors = RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    subtitle: String,
    icon: ImageVector
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun IconChoiceGridItem(
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

@Composable
private fun PeriodChoiceItem(
    label: String,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.surfaceContainerHighest,
        label = "periodBg"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
        else MaterialTheme.colorScheme.onSurface,
        label = "periodContent"
    )

    Surface(
        onClick = onSelect,
        shape = RoundedCornerShape(12.dp),
        color = containerColor,
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = contentColor,
                maxLines = 1,
                softWrap = false
            )
        }
    }
}

@Composable
private fun SeekbarSettingItem(
    label: String,
    value: Float,
    valueDisplay: String,
    valueRange: ClosedFloatingPointRange<Float>,
    step: Float = 1f,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(
                    text = valueDisplay,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    maxLines = 1,
                    softWrap = false
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilledTonalIconButton(
                onClick = {
                    val newValue = (value - step).coerceIn(valueRange.start, valueRange.endInclusive)
                    onValueChange(newValue)
                },
                enabled = value > valueRange.start,
                modifier = Modifier.size(36.dp),
                shape = RoundedCornerShape(12.dp),
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.38f),
                    disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
            ) {
                Icon(
                    imageVector = Icons.Rounded.Remove,
                    contentDescription = "Decrease $label",
                    modifier = Modifier.size(18.dp)
                )
            }

            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = valueRange,
                modifier = Modifier.weight(1f),
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.surfaceContainerHighest
                )
            )

            FilledTonalIconButton(
                onClick = {
                    val newValue = (value + step).coerceIn(valueRange.start, valueRange.endInclusive)
                    onValueChange(newValue)
                },
                enabled = value < valueRange.endInclusive,
                modifier = Modifier.size(36.dp),
                shape = RoundedCornerShape(12.dp),
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.38f),
                    disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Increase $label",
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

