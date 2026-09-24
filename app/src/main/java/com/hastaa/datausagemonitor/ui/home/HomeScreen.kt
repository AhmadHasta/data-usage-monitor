package com.hastaa.datausagemonitor.ui.home

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DataUsage
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hastaa.datausagemonitor.ui.components.AppUsageItem
import com.hastaa.datausagemonitor.ui.components.GlassCard
import com.hastaa.datausagemonitor.ui.components.NetworkStatsRow
import com.hastaa.datausagemonitor.ui.components.QuickSettingsBanner
import com.hastaa.datausagemonitor.ui.components.SectionHeader
import com.hastaa.datausagemonitor.ui.components.UsageCard
import com.hastaa.datausagemonitor.ui.dashboard.DashboardUiState
import com.hastaa.datausagemonitor.ui.theme.AccentPrimary
import com.hastaa.datausagemonitor.ui.theme.DarkBackground
import com.hastaa.datausagemonitor.ui.theme.TextPrimary
import com.hastaa.datausagemonitor.ui.theme.TextSecondary
import com.hastaa.datausagemonitor.ui.theme.TextTertiary
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: DashboardUiState,
    onNavigateToApps: () -> Unit,
    onDismissTileBanner: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val greeting = remember { getGreeting() }
    val topApps = remember(state.filteredApps) {
        state.filteredApps.take(5)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(AccentPrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.DataUsage,
                                contentDescription = null,
                                tint = AccentPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "DataPulse",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                },
                actions = {
                    if (!state.isDemoMode) {
                        IconButton(onClick = onRefresh) {
                            if (state.isRefreshing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = AccentPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Rounded.Refresh,
                                    contentDescription = "Refresh",
                                    tint = TextSecondary
                                )
                            }
                        }
                    }
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header: Greeting and Subtitle
            item(key = "greeting_header") {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Text(
                        text = "$greeting 👋",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Here's your data usage",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary
                    )
                }
            }

            // Quick Settings Prompt Banner (if available)
            if (state.showTileBanner) {
                item(key = "qs_banner") {
                    QuickSettingsBanner(
                        onDismiss = onDismissTileBanner,
                        modifier = Modifier.animateItem()
                    )
                }
            }

            // Focal Today's Usage Card
            item(key = "today_usage_card") {
                UsageCard(
                    totalBytes = state.totalBytes,
                    mobileBytes = state.displayMobileBytes,
                    wifiBytes = state.displayWifiBytes,
                    periodLabel = "TODAY'S USAGE"
                )
            }

            // Network Statistics Summary Row (Mobile & Wi-Fi)
            item(key = "network_stats_row") {
                NetworkStatsRow(
                    mobileBytes = state.displayMobileBytes,
                    wifiBytes = state.displayWifiBytes,
                    periodSubtext = "Today"
                )
            }

            // Top Applications Section Header
            item(key = "top_apps_header") {
                SectionHeader(
                    title = "Top Applications",
                    actionLabel = "See all →",
                    onActionClick = onNavigateToApps,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Top Applications Preview List
            if (state.isLoading && !state.isDemoMode) {
                item(key = "loading_apps") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = AccentPrimary)
                    }
                }
            } else if (topApps.isEmpty()) {
                item(key = "empty_apps") {
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No application data recorded today",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                        }
                    }
                }
            } else {
                items(
                    items = topApps,
                    key = { it.packageName + it.uid }
                ) { app ->
                    AppUsageItem(
                        app = app,
                        maxUsageBytes = state.maxAppUsageBytes,
                        modifier = Modifier.animateItem()
                    )
                }
            }

            item(key = "bottom_spacer") {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

private fun getGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 5..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        in 17..20 -> "Good evening"
        else -> "Good night"
    }
}
