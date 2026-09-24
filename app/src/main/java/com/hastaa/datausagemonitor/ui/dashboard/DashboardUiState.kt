package com.hastaa.datausagemonitor.ui.dashboard

import com.hastaa.datausagemonitor.data.DummyData
import com.hastaa.datausagemonitor.domain.model.AppDataUsage
import com.hastaa.datausagemonitor.domain.model.UsagePeriod

enum class NetworkFilter(val label: String) {
    ALL("All"),
    MOBILE("Mobile"),
    WIFI("Wi-Fi")
}

data class DashboardUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val hasUsageAccess: Boolean = false,
    val period: UsagePeriod = UsagePeriod.TODAY,
    val mobileBytes: Long = 0L,
    val wifiBytes: Long = 0L,
    val apps: List<AppDataUsage> = emptyList(),
    val searchQuery: String = "",
    val networkFilter: NetworkFilter = NetworkFilter.ALL,
    val showTileBanner: Boolean = false,
    val isDemoMode: Boolean = false,
    val error: String? = null
) {
    // Determine active mobile/wifi bytes (Demo mode fallback or real data)
    val displayMobileBytes: Long
        get() = if (isDemoMode) DummyData.mobileBytes else mobileBytes

    val displayWifiBytes: Long
        get() = if (isDemoMode) DummyData.wifiBytes else wifiBytes

    val totalBytes: Long
        get() = displayMobileBytes + displayWifiBytes

    val activeApps: List<AppDataUsage>
        get() = if (isDemoMode) DummyData.apps else apps

    val filteredApps: List<AppDataUsage>
        get() {
            return activeApps
                .filter { app ->
                    val matchesQuery = searchQuery.isBlank() ||
                            app.appName.contains(searchQuery, ignoreCase = true) ||
                            app.packageName.contains(searchQuery, ignoreCase = true)

                    val matchesNetwork = when (networkFilter) {
                        NetworkFilter.ALL -> app.totalBytes > 0L
                        NetworkFilter.MOBILE -> app.mobileBytes > 0L
                        NetworkFilter.WIFI -> app.wifiBytes > 0L
                    }

                    matchesQuery && matchesNetwork
                }
                .sortedByDescending {
                    when (networkFilter) {
                        NetworkFilter.ALL -> it.totalBytes
                        NetworkFilter.MOBILE -> it.mobileBytes
                        NetworkFilter.WIFI -> it.wifiBytes
                    }
                }
        }

    val maxAppUsageBytes: Long
        get() = filteredApps.firstOrNull()?.let {
            when (networkFilter) {
                NetworkFilter.ALL -> it.totalBytes
                NetworkFilter.MOBILE -> it.mobileBytes
                NetworkFilter.WIFI -> it.wifiBytes
            }
        } ?: 1L
}
