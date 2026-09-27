package com.hastaa.datausagemonitor.ui.screen.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.hastaa.datausagemonitor.domain.model.UsagePeriod
import com.hastaa.datausagemonitor.ui.components.AppUsageItem
import com.hastaa.datausagemonitor.ui.components.HeroUsageCard
import com.hastaa.datausagemonitor.ui.components.NetworkMetricsRow
import com.hastaa.datausagemonitor.ui.components.PeriodSelector
import com.hastaa.datausagemonitor.ui.components.QuickSettingsBanner
import com.hastaa.datausagemonitor.ui.screen.dashboard.components.AppUsageSectionHeader
import com.hastaa.datausagemonitor.ui.screen.dashboard.components.DashboardEmptyState
import com.hastaa.datausagemonitor.ui.screen.dashboard.components.DashboardLoadingIndicator
import com.hastaa.datausagemonitor.ui.screen.dashboard.components.DashboardTopAppBar
import com.hastaa.datausagemonitor.widget.DataUsageAppWidgetProvider

@Composable
fun DashboardScreen(
    state: DashboardUiState,
    onPeriodSelected: (UsagePeriod) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onNetworkFilterChanged: (NetworkFilter) -> Unit,
    onDismissTileBanner: () -> Unit,
    onRefresh: () -> Unit,
    onOpenTileSettings: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isSearchExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            DashboardTopAppBar(
                isRefreshing = state.isRefreshing,
                onOpenTileSettings = onOpenTileSettings,
                onRefresh = {
                    onRefresh()
                    DataUsageAppWidgetProvider.notifyDataChanged(context)
                }
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (state.showTileBanner) {
                item(key = "qs_banner") {
                    QuickSettingsBanner(
                        onDismiss = onDismissTileBanner,
                        onCustomizeTile = onOpenTileSettings,
                        modifier = Modifier.animateItem()
                    )
                }
            }

            item(key = "period_selector") {
                PeriodSelector(
                    selectedPeriod = state.period,
                    onPeriodSelected = onPeriodSelected
                )
            }

            item(key = "hero_card") {
                HeroUsageCard(
                    totalBytes = state.totalBytes,
                    mobileBytes = state.mobileBytes,
                    wifiBytes = state.wifiBytes,
                    period = state.period
                )
            }

            item(key = "network_metrics") {
                NetworkMetricsRow(
                    mobileBytes = state.mobileBytes,
                    wifiBytes = state.wifiBytes
                )
            }

            item(key = "apps_header") {
                AppUsageSectionHeader(
                    appCount = state.filteredApps.size,
                    searchQuery = state.searchQuery,
                    onSearchQueryChanged = onSearchQueryChanged,
                    isSearchExpanded = isSearchExpanded,
                    onToggleSearch = { isSearchExpanded = !isSearchExpanded },
                    selectedFilter = state.networkFilter,
                    onNetworkFilterChanged = onNetworkFilterChanged
                )
            }

            if (state.isLoading) {
                item(key = "loading_indicator") {
                    DashboardLoadingIndicator()
                }
            } else if (state.filteredApps.isEmpty()) {
                item(key = "empty_state") {
                    DashboardEmptyState(searchQuery = state.searchQuery)
                }
            } else {
                items(
                    items = state.filteredApps,
                    key = { "${it.uid}_${it.packageName}" }
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
