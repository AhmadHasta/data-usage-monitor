package com.hastaa.datausagemonitor.ui.settings

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.NotificationsNone
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material.icons.rounded.Widgets
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hastaa.datausagemonitor.ui.components.GlassCard
import com.hastaa.datausagemonitor.ui.dashboard.DashboardUiState
import com.hastaa.datausagemonitor.ui.theme.AccentPrimary
import com.hastaa.datausagemonitor.ui.theme.AccentSecondary
import com.hastaa.datausagemonitor.ui.theme.AccentSuccess
import com.hastaa.datausagemonitor.ui.theme.DarkBackground
import com.hastaa.datausagemonitor.ui.theme.DarkSurfaceBorder
import com.hastaa.datausagemonitor.ui.theme.DarkSurfaceElevated
import com.hastaa.datausagemonitor.ui.theme.TextPrimary
import com.hastaa.datausagemonitor.ui.theme.TextSecondary
import com.hastaa.datausagemonitor.ui.theme.TextTertiary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    state: DashboardUiState,
    onToggleDemoMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground
                )
            )
        },
        containerColor = DarkBackground,
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Section: Appearance
            item(key = "section_appearance") {
                SettingsSection(title = "Appearance") {
                    SettingsItemRow(
                        icon = Icons.Rounded.DarkMode,
                        iconTint = AccentPrimary,
                        title = "Dark Theme",
                        subtitle = "OLED System Dark UI",
                        trailingContent = {
                            Text(
                                text = "Enabled",
                                style = MaterialTheme.typography.labelMedium,
                                color = AccentPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    )

                    SettingsItemDivider()

                    SettingsItemRow(
                        icon = Icons.Rounded.Palette,
                        iconTint = AccentSecondary,
                        title = "Accent Color",
                        subtitle = "Electric Blue & Purple Gradient",
                        trailingContent = {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(AccentPrimary)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(AccentSecondary)
                                )
                            }
                        }
                    )
                }
            }

            // Section: Data & Preview Mode
            item(key = "section_data_mode") {
                SettingsSection(title = "Data Preview") {
                    SettingsItemRow(
                        icon = Icons.Rounded.SwapHoriz,
                        iconTint = AccentSuccess,
                        title = "Demo Data Mode",
                        subtitle = if (state.isDemoMode)
                            "Showing Phase 0 dummy data (1.42 GB)"
                        else "Showing active device usage",
                        trailingContent = {
                            Switch(
                                checked = state.isDemoMode,
                                onCheckedChange = { onToggleDemoMode() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = TextPrimary,
                                    checkedTrackColor = AccentPrimary,
                                    uncheckedThumbColor = TextSecondary,
                                    uncheckedTrackColor = DarkSurfaceElevated
                                )
                            )
                        }
                    )
                }
            }

            // Section: General & About
            item(key = "section_general") {
                SettingsSection(title = "General") {
                    SettingsItemRow(
                        icon = Icons.Rounded.Info,
                        iconTint = AccentPrimary,
                        title = "About DataPulse",
                        subtitle = "Phase 0 — UI Foundation",
                        trailingContent = {
                            Text(
                                text = "v0.1.0",
                                style = MaterialTheme.typography.labelMedium,
                                color = TextSecondary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    )

                    SettingsItemDivider()

                    SettingsItemRow(
                        icon = Icons.Rounded.Security,
                        iconTint = AccentSuccess,
                        title = "Privacy",
                        subtitle = "100% on-device • Zero internet permission",
                        trailingContent = {
                            Text(
                                text = "Secure",
                                style = MaterialTheme.typography.labelSmall,
                                color = AccentSuccess,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    )
                }
            }

            // Section: Upcoming Features (Phase 1 & 2)
            item(key = "section_upcoming") {
                SettingsSection(title = "Upcoming Features") {
                    SettingsItemRow(
                        icon = Icons.Rounded.Speed,
                        iconTint = TextTertiary,
                        title = "Monthly Data Limits",
                        subtitle = "Billing cycle & threshold alerts",
                        trailingContent = {
                            BadgePill(text = "Phase 1")
                        }
                    )

                    SettingsItemDivider()

                    SettingsItemRow(
                        icon = Icons.Rounded.NotificationsNone,
                        iconTint = TextTertiary,
                        title = "Daily Usage Warnings",
                        subtitle = "Configurable notification alerts",
                        trailingContent = {
                            BadgePill(text = "Phase 1")
                        }
                    )
                }
            }

            item(key = "bottom_space") {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = TextSecondary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
private fun SettingsItemRow(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    trailingContent: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(iconTint.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                color = TextSecondary
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        trailingContent()
    }
}

@Composable
private fun SettingsItemDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .padding(horizontal = 16.dp)
            .background(DarkSurfaceBorder.copy(alpha = 0.5f))
    )
}

@Composable
private fun BadgePill(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(DarkSurfaceElevated)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = TextTertiary,
            fontWeight = FontWeight.SemiBold
        )
    }
}
