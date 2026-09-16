package com.hastaa.datausagemonitor.data.network

import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.os.Process
import com.hastaa.datausagemonitor.domain.model.AppDataUsage
import com.hastaa.datausagemonitor.domain.model.NetworkUsageSummary
import com.hastaa.datausagemonitor.domain.model.UsagePeriod

class NetworkStatsDataSource(private val context: Context) {

    private val networkStatsManager: NetworkStatsManager? by lazy {
        context.getSystemService(Context.NETWORK_STATS_SERVICE) as? NetworkStatsManager
    }

    private val packageManager: PackageManager by lazy {
        context.packageManager
    }

    /**
     * Queries total mobile and Wi-Fi data usage for the device over the specified period.
     */
    fun queryDeviceSummary(period: UsagePeriod): NetworkUsageSummary {
        val (startTime, endTime) = period.getTimeRange()
        val mobileBytes = querySummaryForNetworkType(ConnectivityManager.TYPE_MOBILE, startTime, endTime)
        val wifiBytes = querySummaryForNetworkType(ConnectivityManager.TYPE_WIFI, startTime, endTime)

        return NetworkUsageSummary(
            mobileBytes = mobileBytes,
            wifiBytes = wifiBytes,
            period = period,
            lastUpdatedMillis = System.currentTimeMillis()
        )
    }

    /**
     * Queries per-application network usage across mobile data and Wi-Fi.
     * Results are aggregated by UID and sorted by highest total usage first.
     */
    fun queryAppUsage(period: UsagePeriod): List<AppDataUsage> {
        val (startTime, endTime) = period.getTimeRange()

        // UID -> Pair(mobileBytes, wifiBytes)
        val usageByUid = mutableMapOf<Int, LongArray>() // LongArray(2): [0]=mobile, [1]=wifi

        collectUidUsage(ConnectivityManager.TYPE_MOBILE, startTime, endTime) { uid, rx, tx ->
            val arr = usageByUid.getOrPut(uid) { LongArray(2) }
            arr[0] += (rx + tx)
        }

        collectUidUsage(ConnectivityManager.TYPE_WIFI, startTime, endTime) { uid, rx, tx ->
            val arr = usageByUid.getOrPut(uid) { LongArray(2) }
            arr[1] += (rx + tx)
        }

        val appUsageList = mutableListOf<AppDataUsage>()

        for ((uid, stats) in usageByUid) {
            val mobileBytes = stats[0]
            val wifiBytes = stats[1]
            if (mobileBytes + wifiBytes <= 0L) continue

            val appInfo = resolveUidDetails(uid)
            appUsageList.add(
                AppDataUsage(
                    uid = uid,
                    packageName = appInfo.packageName,
                    appName = appInfo.appName,
                    icon = appInfo.icon,
                    mobileBytes = mobileBytes,
                    wifiBytes = wifiBytes,
                    isSystemApp = appInfo.isSystemApp
                )
            )
        }

        // Sort highest usage to lowest usage
        return appUsageList.sortedByDescending { it.totalBytes }
    }

    private fun querySummaryForNetworkType(networkType: Int, startTime: Long, endTime: Long): Long {
        val manager = networkStatsManager ?: return 0L
        return try {
            val bucket = manager.querySummaryForDevice(networkType, null, startTime, endTime)
            bucket.rxBytes + bucket.txBytes
        } catch (e: Exception) {
            // SecurityException, RemoteException, or unsupported device configuration
            0L
        }
    }

    private inline fun collectUidUsage(
        networkType: Int,
        startTime: Long,
        endTime: Long,
        onBucket: (uid: Int, rx: Long, tx: Long) -> Unit
    ) {
        val manager = networkStatsManager ?: return
        var stats: NetworkStats? = null
        try {
            stats = manager.querySummary(networkType, null, startTime, endTime)
            val bucket = NetworkStats.Bucket()
            while (stats.hasNextBucket()) {
                stats.getNextBucket(bucket)
                if (bucket.rxBytes > 0 || bucket.txBytes > 0) {
                    onBucket(bucket.uid, bucket.rxBytes, bucket.txBytes)
                }
            }
        } catch (e: Exception) {
            // Gracefully catch SecurityException if permission revoked or OEM quirks
        } finally {
            stats?.close()
        }
    }

    private data class UidDetails(
        val packageName: String,
        val appName: String,
        val icon: Drawable?,
        val isSystemApp: Boolean
    )

    private fun resolveUidDetails(uid: Int): UidDetails {
        // Handle well-known Android special UIDs
        when (uid) {
            Process.SYSTEM_UID -> return UidDetails(
                packageName = "android",
                appName = "Android System",
                icon = null,
                isSystemApp = true
            )
            0 -> return UidDetails(
                packageName = "root",
                appName = "OS & Kernel",
                icon = null,
                isSystemApp = true
            )
            1052, 1020 -> return UidDetails(
                packageName = "com.android.networkstack",
                appName = "Network Stack / DNS",
                icon = null,
                isSystemApp = true
            )
            -4 -> return UidDetails(
                packageName = "removed",
                appName = "Removed Applications",
                icon = null,
                isSystemApp = true
            )
            -5 -> return UidDetails(
                packageName = "tethering",
                appName = "Hotspot / Tethering",
                icon = null,
                isSystemApp = true
            )
        }

        // Try mapping via PackageManager
        val packages = try {
            packageManager.getPackagesForUid(uid)
        } catch (e: Exception) {
            null
        }

        if (!packages.isNullOrEmpty()) {
            val primaryPackage = packages[0]
            try {
                val applicationInfo = packageManager.getApplicationInfo(primaryPackage, 0)
                val label = packageManager.getApplicationLabel(applicationInfo).toString()
                val icon = try {
                    packageManager.getApplicationIcon(applicationInfo)
                } catch (e: Exception) {
                    null
                }
                val isSystem = (applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                return UidDetails(
                    packageName = primaryPackage,
                    appName = label.ifBlank { primaryPackage },
                    icon = icon,
                    isSystemApp = isSystem
                )
            } catch (e: PackageManager.NameNotFoundException) {
                // Application may have been uninstalled or isolated
                return UidDetails(
                    packageName = primaryPackage,
                    appName = primaryPackage,
                    icon = null,
                    isSystemApp = false
                )
            }
        }

        // Fallback when UID has no associated packages
        return UidDetails(
            packageName = "uid.$uid",
            appName = "Application (UID $uid)",
            icon = null,
            isSystemApp = uid < 10000
        )
    }
}
