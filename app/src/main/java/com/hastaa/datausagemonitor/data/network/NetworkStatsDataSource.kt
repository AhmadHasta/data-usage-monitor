package com.hastaa.datausagemonitor.data.network

import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.os.Process
import android.util.LruCache
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
     * Queries per-app data usage breakdown by UID and resolves package details.
     */
    fun queryAppUsage(period: UsagePeriod): List<AppDataUsage> {
        val (startTime, endTime) = period.getTimeRange()

        // Map: UID -> Pair(mobileBytes, wifiBytes)
        val usageByUid = mutableMapOf<Int, LongArray>()

        // 1. Mobile data stats
        queryStatsForNetwork(ConnectivityManager.TYPE_MOBILE, startTime, endTime, usageByUid, isMobile = true)

        // 2. Wi-Fi data stats
        queryStatsForNetwork(ConnectivityManager.TYPE_WIFI, startTime, endTime, usageByUid, isMobile = false)

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
                    iconBitmap = appInfo.iconBitmap,
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

    private fun queryStatsForNetwork(
        networkType: Int,
        startTime: Long,
        endTime: Long,
        usageMap: MutableMap<Int, LongArray>,
        isMobile: Boolean
    ) {
        val manager = networkStatsManager ?: return
        val stats: NetworkStats = try {
            manager.querySummary(networkType, null, startTime, endTime)
        } catch (e: Exception) {
            return
        }

        val bucket = NetworkStats.Bucket()
        while (stats.hasNextBucket()) {
            stats.getNextBucket(bucket)
            val totalBytes = bucket.rxBytes + bucket.txBytes
            if (totalBytes <= 0L) continue

            val entry = usageMap.getOrPut(bucket.uid) { LongArray(2) }
            if (isMobile) {
                entry[0] += totalBytes
            } else {
                entry[1] += totalBytes
            }
        }
        stats.close()
    }

    private data class UidDetails(
        val packageName: String,
        val appName: String,
        val icon: Drawable?,
        val iconBitmap: Bitmap?,
        val isSystemApp: Boolean
    )

    private fun resolveUidDetails(uid: Int): UidDetails {
        // Handle well-known Android special UIDs
        when (uid) {
            Process.SYSTEM_UID -> return UidDetails(
                packageName = "android",
                appName = "Android System",
                icon = null,
                iconBitmap = null,
                isSystemApp = true
            )
            0 -> return UidDetails(
                packageName = "root",
                appName = "OS & Kernel",
                icon = null,
                iconBitmap = null,
                isSystemApp = true
            )
            1052, 1020 -> return UidDetails(
                packageName = "com.android.networkstack",
                appName = "Network Stack / DNS",
                icon = null,
                iconBitmap = null,
                isSystemApp = true
            )
            -4 -> return UidDetails(
                packageName = "removed",
                appName = "Removed Applications",
                icon = null,
                iconBitmap = null,
                isSystemApp = true
            )
            -5 -> return UidDetails(
                packageName = "tethering",
                appName = "Hotspot / Tethering",
                icon = null,
                iconBitmap = null,
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
                val cached = iconBitmapCache.get(primaryPackage)
                val iconBitmap = if (cached != null) {
                    cached
                } else {
                    val d = try {
                        packageManager.getApplicationIcon(applicationInfo)
                    } catch (e: Exception) {
                        null
                    }
                    d?.let { drawableToBitmap(it) }?.also {
                        iconBitmapCache.put(primaryPackage, it)
                    }
                }
                val isSystem = (applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                return UidDetails(
                    packageName = primaryPackage,
                    appName = label.ifBlank { primaryPackage },
                    icon = null,
                    iconBitmap = iconBitmap,
                    isSystemApp = isSystem
                )
            } catch (e: PackageManager.NameNotFoundException) {
                // Application may have been uninstalled or isolated
                return UidDetails(
                    packageName = primaryPackage,
                    appName = primaryPackage,
                    icon = null,
                    iconBitmap = null,
                    isSystemApp = false
                )
            }
        }

        // Fallback when UID has no associated packages
        return UidDetails(
            packageName = "uid.$uid",
            appName = "Application (UID $uid)",
            icon = null,
            iconBitmap = null,
            isSystemApp = uid < 10000
        )
    }

    companion object {
        private val iconBitmapCache = LruCache<String, Bitmap>(250)

        private fun drawableToBitmap(drawable: Drawable): Bitmap {
            if (drawable is BitmapDrawable && drawable.bitmap != null) {
                return drawable.bitmap
            }
            val width = if (drawable.intrinsicWidth in 1..256) drawable.intrinsicWidth else 128
            val height = if (drawable.intrinsicHeight in 1..256) drawable.intrinsicHeight else 128
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            return bitmap
        }
    }
}
