package com.hastaa.datausagemonitor.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DataUsage
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hastaa.datausagemonitor.domain.model.UsagePeriod
import com.hastaa.datausagemonitor.ui.components.AppUsageItem
import com.hastaa.datausagemonitor.ui.components.HeroUsageCard
import com.hastaa.datausagemonitor.ui.components.NetworkMetricsRow
import com.hastaa.datausagemonitor.ui.components.PeriodSelector
import com.hastaa.datausagemonitor.ui.components.QuickSettingsBanner
import com.hastaa.datausagemonitor.ui.dashboard.DashboardUiState
import com.hastaa.datausagemonitor.ui.dashboard.NetworkFilter
import com.hastaa.datausagemonitor.ui.theme.CyanNeon
import com.hastaa.datausagemonitor.ui.theme.SurfaceBorderDark
import com.hastaa.datausagemonitor.ui.theme.TextSecondary
import com.hastaa.datausagemonitor.ui.theme.TextTertiary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    state: DashboardUiState,
    onPeriodSelected: (UsagePeriod) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onNetworkFilterChanged: (NetworkFilter) -> Unit,
    onDismissTileBanner: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isSearchExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(CyanNeon.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.DataUsage,
                                contentDescription = null,
                                tint = CyanNeon,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Data Usage Monitor",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onRefresh) {
                        if (state.isRefreshing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = CyanNeon,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Rounded.Refresh,
                                contentDescription = "Refresh",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Quick Settings Prompt Banner
            if (state.showTileBanner) {
                item(key = "qs_banner") {
                    QuickSettingsBanner(
                        onDismiss = onDismissTileBanner,
                        modifier = Modifier.animateItem()
                    )
                }
            }

            // Period Selector (Today / Week / Month)
            item(key = "period_selector") {
                PeriodSelector(
                    selectedPeriod = state.period,
                    onPeriodSelected = onPeriodSelected
                )
            }

            // Hero Card (Total Usage & Visual Ratio)
            item(key = "hero_card") {
                HeroUsageCard(
                    totalBytes = state.totalBytes,
                    mobileBytes = state.mobileBytes,
                    wifiBytes = state.wifiBytes,
                    period = state.period
                )
            }

            // Network Category Row (Mobile vs Wi-Fi)
            item(key = "network_metrics") {
                NetworkMetricsRow(
                    mobileBytes = state.mobileBytes,
                    wifiBytes = state.wifiBytes
                )
            }

            // Applications Section Header & Filter Controls
            item(key = "apps_header") {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Application Usage",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = state.filteredApps.size.toString(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CyanNeon,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        IconButton(onClick = { isSearchExpanded = !isSearchExpanded }) {
                            Icon(
                                imageVector = if (isSearchExpanded) Icons.Rounded.Close else Icons.Rounded.Search,
                                contentDescription = "Toggle Search",
                                tint = if (isSearchExpanded) CyanNeon else TextSecondary
                            )
                        }
                    }

                    // Expandable Search Bar
                    AnimatedVisibility(visible = isSearchExpanded) {
                        TextField(
                            value = state.searchQuery,
                            onValueChange = onSearchQueryChanged,
                            placeholder = {
                                Text("Search applications...", color = TextTertiary)
                            },
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                cursorColor = CyanNeon
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp)
                                .border(1.dp, SurfaceBorderDark, RoundedCornerShape(14.dp))
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Filter Chips (All, Mobile, Wi-Fi)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        NetworkFilter.values().forEach { filter ->
                            val isSelected = filter == state.networkFilter
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (isSelected) CyanNeon.copy(alpha = 0.2f)
                                        else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) CyanNeon else SurfaceBorderDark,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable { onNetworkFilterChanged(filter) }
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = filter.label,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) CyanNeon else TextSecondary
                                )
                            }
                        }
                    }
                }
            }

            // Application List Items
            if (state.isLoading) {
                item(key = "loading_indicator") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = CyanNeon)
                    }
                }
            } else if (state.filteredApps.isEmpty()) {
                item(key = "empty_state") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (state.searchQuery.isNotBlank())
                                "No applications match \"${state.searchQuery}\""
                            else "No network usage recorded for this period",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextSecondary
                        )
                    }
                }
            } else {
                items(
                    items = state.filteredApps,
                    key = { it.uid }
                ) { app ->
                    AppUsageItem(
                        app = app,
                        maxUsageBytes = state.maxAppUsageBytes,
                        modifier = Modifier.animateItem()
                    )
                }
            }

            item(key = "footer_spacer") {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
