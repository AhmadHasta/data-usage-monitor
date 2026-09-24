package com.hastaa.datausagemonitor.ui.apps

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
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
import com.hastaa.datausagemonitor.ui.components.GlassCard
import com.hastaa.datausagemonitor.ui.components.PeriodSelector
import com.hastaa.datausagemonitor.ui.dashboard.DashboardUiState
import com.hastaa.datausagemonitor.ui.dashboard.NetworkFilter
import com.hastaa.datausagemonitor.ui.theme.AccentPrimary
import com.hastaa.datausagemonitor.ui.theme.DarkBackground
import com.hastaa.datausagemonitor.ui.theme.DarkSurfaceBorder
import com.hastaa.datausagemonitor.ui.theme.DarkSurfaceElevated
import com.hastaa.datausagemonitor.ui.theme.TextPrimary
import com.hastaa.datausagemonitor.ui.theme.TextSecondary
import com.hastaa.datausagemonitor.ui.theme.TextTertiary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppsScreen(
    state: DashboardUiState,
    onPeriodSelected: (UsagePeriod) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onNetworkFilterChanged: (NetworkFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    var isSearchExpanded by remember { mutableStateOf(state.searchQuery.isNotBlank()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Applications",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(DarkSurfaceElevated)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = state.filteredApps.size.toString(),
                                style = MaterialTheme.typography.labelSmall,
                                color = AccentPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { isSearchExpanded = !isSearchExpanded }) {
                        Icon(
                            imageVector = if (isSearchExpanded) Icons.Rounded.Close else Icons.Rounded.Search,
                            contentDescription = "Search",
                            tint = if (isSearchExpanded) AccentPrimary else TextSecondary
                        )
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Expandable Search Input
            if (isSearchExpanded) {
                item(key = "search_field") {
                    TextField(
                        value = state.searchQuery,
                        onValueChange = onSearchQueryChanged,
                        placeholder = {
                            Text("Search application name...", color = TextTertiary)
                        },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = DarkSurfaceElevated,
                            unfocusedContainerColor = DarkSurfaceElevated,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            cursorColor = AccentPrimary
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(14.dp))
                    )
                }
            }

            // Period Selector Tab (Today / Week / Month)
            if (!state.isDemoMode) {
                item(key = "period_selector") {
                    PeriodSelector(
                        selectedPeriod = state.period,
                        onPeriodSelected = onPeriodSelected
                    )
                }
            }

            // Network Filter Chips (All, Mobile, Wi-Fi)
            item(key = "network_filter_chips") {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    NetworkFilter.values().forEach { filter ->
                        val isSelected = filter == state.networkFilter
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) AccentPrimary.copy(alpha = 0.18f)
                                    else DarkSurfaceElevated
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) AccentPrimary else DarkSurfaceBorder,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { onNetworkFilterChanged(filter) }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = filter.label,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) AccentPrimary else TextSecondary
                            )
                        }
                    }
                }
            }

            // App List Items
            if (state.isLoading && !state.isDemoMode) {
                item(key = "loading_apps") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = AccentPrimary)
                    }
                }
            } else if (state.filteredApps.isEmpty()) {
                item(key = "empty_state") {
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (state.searchQuery.isNotBlank())
                                    "No applications found matching \"${state.searchQuery}\""
                                else "No data usage recorded",
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextSecondary
                            )
                        }
                    }
                }
            } else {
                items(
                    items = state.filteredApps,
                    key = { it.packageName + it.uid }
                ) { app ->
                    AppUsageItem(
                        app = app,
                        maxUsageBytes = state.maxAppUsageBytes,
                        showDetailedBreakdown = true,
                        modifier = Modifier.animateItem()
                    )
                }
            }

            item(key = "bottom_padding") {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
