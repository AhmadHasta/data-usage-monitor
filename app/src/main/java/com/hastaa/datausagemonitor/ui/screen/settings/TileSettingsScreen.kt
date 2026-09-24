package com.hastaa.datausagemonitor.ui.screen.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hastaa.datausagemonitor.data.local.AppTheme
import com.hastaa.datausagemonitor.domain.model.TileContentStyle
import com.hastaa.datausagemonitor.domain.model.TileTextLayout
import com.hastaa.datausagemonitor.ui.screen.settings.components.AppThemeChoiceCard
import com.hastaa.datausagemonitor.ui.screen.settings.components.ContentStyleChoiceCard
import com.hastaa.datausagemonitor.ui.screen.settings.components.SectionHeader
import com.hastaa.datausagemonitor.ui.screen.settings.components.TextLayoutChoiceCard
import com.hastaa.datausagemonitor.ui.screen.settings.sections.TileIconChoiceSection
import com.hastaa.datausagemonitor.ui.screen.settings.sections.TilePeriodSection
import com.hastaa.datausagemonitor.ui.screen.settings.sections.TileQuotaSection
import com.hastaa.datausagemonitor.ui.screen.settings.sections.TileSizingSection

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
                            text = "Appearance",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Customize app theme & Quick Settings tile",
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
            item(key = "section_app_theme") {
                SectionHeader(
                    title = "App Theme",
                    subtitle = "Select application visual style and color system",
                    icon = Icons.Rounded.Palette
                )
                Spacer(modifier = Modifier.height(10.dp))
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    AppTheme.values().forEach { theme ->
                        AppThemeChoiceCard(
                            theme = theme,
                            isSelected = state.appTheme == theme,
                            onSelect = { viewModel.setAppTheme(theme) }
                        )
                    }
                }
            }

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
                TileSizingSection(
                    config = config,
                    onResetStyleSizing = { viewModel.resetStyleSizing(it) },
                    onMetricIconSizeChange = { viewModel.setMetricIconSize(it) },
                    onIconStrokeWidthChange = { viewModel.setIconStrokeWidth(it) },
                    onMetricValueTextSizeChange = { viewModel.setMetricValueTextSize(it) },
                    onMetricUnitTextSizeChange = { viewModel.setMetricUnitTextSize(it) },
                    onMetricSpacingChange = { viewModel.setMetricSpacing(it) },
                    onRingDiameterChange = { viewModel.setRingDiameter(it) },
                    onRingStrokeWidthChange = { viewModel.setRingStrokeWidth(it) },
                    onRingSweepAngleChange = { viewModel.setRingSweepAngle(it) },
                    onProgressIconSizeChange = { viewModel.setProgressIconSize(it) },
                    onProgressRingDiameterChange = { viewModel.setProgressRingDiameter(it) },
                    onProgressRingStrokeChange = { viewModel.setProgressRingStroke(it) },
                    onIconOnlySizeChange = { viewModel.setIconOnlySize(it) }
                )
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
                TileIconChoiceSection(
                    selectedChoice = config.iconChoice,
                    onSelectChoice = { viewModel.setIconChoice(it) }
                )
            }

            item(key = "section_period") {
                TilePeriodSection(
                    selectedPeriod = config.period,
                    onSelectPeriod = { viewModel.setPeriod(it) }
                )
            }

            item(key = "section_quota_limit") {
                TileQuotaSection(
                    config = config,
                    onQuotaLimitChange = { viewModel.setQuotaLimit(it) }
                )
            }

            item(key = "bottom_space") {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
