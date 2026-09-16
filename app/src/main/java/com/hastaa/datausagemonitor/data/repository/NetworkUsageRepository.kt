package com.hastaa.datausagemonitor.data.repository

import android.content.Context
import com.hastaa.datausagemonitor.data.local.UsageCache
import com.hastaa.datausagemonitor.data.network.NetworkStatsDataSource
import com.hastaa.datausagemonitor.domain.model.AppDataUsage
import com.hastaa.datausagemonitor.domain.model.NetworkUsageSummary
import com.hastaa.datausagemonitor.domain.model.UsagePeriod
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class NetworkUsageRepository(context: Context) {

    private val dataSource = NetworkStatsDataSource(context.applicationContext)
    private val cache = UsageCache(context.applicationContext)

    val dismissedTilePromptFlow: Flow<Boolean> = cache.dismissedTilePromptFlow

    suspend fun getCachedTodayUsage(): NetworkUsageSummary = withContext(Dispatchers.IO) {
        cache.getTodayUsage()
    }

    suspend fun getDeviceSummary(period: UsagePeriod): NetworkUsageSummary = withContext(Dispatchers.IO) {
        val summary = dataSource.queryDeviceSummary(period)
        if (period == UsagePeriod.TODAY) {
            cache.saveTodayUsage(summary.mobileBytes, summary.wifiBytes)
        }
        summary
    }

    suspend fun getAppUsage(period: UsagePeriod): List<AppDataUsage> = withContext(Dispatchers.IO) {
        dataSource.queryAppUsage(period)
    }

    suspend fun setDismissedTilePrompt(dismissed: Boolean) = withContext(Dispatchers.IO) {
        cache.setDismissedTilePrompt(dismissed)
    }
}
