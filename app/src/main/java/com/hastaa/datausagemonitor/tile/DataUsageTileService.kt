package com.hastaa.datausagemonitor.tile

import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.graphics.drawable.Icon
import com.hastaa.datausagemonitor.MainActivity
import com.hastaa.datausagemonitor.R
import com.hastaa.datausagemonitor.data.repository.NetworkUsageRepository
import com.hastaa.datausagemonitor.domain.model.UsagePeriod
import com.hastaa.datausagemonitor.util.ActiveNetworkType
import com.hastaa.datausagemonitor.util.ByteFormatter
import com.hastaa.datausagemonitor.util.NetworkTypeHelper
import com.hastaa.datausagemonitor.util.PermissionHelper
import com.hastaa.datausagemonitor.util.TileIconGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Quick Settings Tile Service that dynamically presents today's network usage
 * based on the active connection (Mobile Data vs. Wi-Fi).
 * Utilizes Active Tile mode with cached fallback for instant rendering.
 */
class DataUsageTileService : TileService() {

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var refreshJob: Job? = null
    private val repository by lazy { NetworkUsageRepository(this) }

    override fun onStartListening() {
        super.onStartListening()
        try {
            val networkType = NetworkTypeHelper.getActiveNetworkType(this)

            // 1. Immediately display cached value for active network to prevent lag
            serviceScope.launch {
                try {
                    val cached = repository.getCachedTodayUsage()
                    val bytesToShow = when (networkType) {
                        ActiveNetworkType.WIFI -> cached.wifiBytes
                        ActiveNetworkType.MOBILE, ActiveNetworkType.OFFLINE -> cached.mobileBytes
                    }
                    applyTileState(bytesToShow, networkType)
                } catch (t: Throwable) {
                    // Safe fallback
                }
            }

            // 2. Perform lightweight background device summary query to keep tile up-to-date
            refreshJob?.cancel()
            refreshJob = serviceScope.launch {
                try {
                    val summary = repository.getDeviceSummary(UsagePeriod.TODAY)
                    val currentNetwork = NetworkTypeHelper.getActiveNetworkType(this@DataUsageTileService)
                    val bytesToShow = when (currentNetwork) {
                        ActiveNetworkType.WIFI -> summary.wifiBytes
                        ActiveNetworkType.MOBILE, ActiveNetworkType.OFFLINE -> summary.mobileBytes
                    }
                    applyTileState(bytesToShow, currentNetwork)
                } catch (t: Throwable) {
                    // Safe fallback
                }
            }
        } catch (t: Throwable) {
            // Guard against any unexpected system framework exceptions
        }
    }

    override fun onStopListening() {
        refreshJob?.cancel()
        super.onStopListening()
    }

    override fun onClick() {
        super.onClick()
        try {
            val launchIntent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                val pendingIntent = PendingIntent.getActivity(
                    this,
                    0,
                    launchIntent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
                startActivityAndCollapse(pendingIntent)
            } else {
                @Suppress("DEPRECATION")
                startActivityAndCollapse(launchIntent)
            }
        } catch (t: Throwable) {
            // Fallback for OEMs with non-standard TileService activity launcher behavior
            try {
                val fallbackIntent = Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                startActivity(fallbackIntent)
            } catch (ignored: Throwable) {}
        }
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

    private fun applyTileState(todayBytes: Long, networkType: ActiveNetworkType) {
        val tile = qsTile ?: return

        if (!PermissionHelper.hasUsageAccess(this)) {
            tile.state = Tile.STATE_INACTIVE
            tile.label = "Need Access"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                tile.subtitle = "Tap to setup"
            }
            try {
                tile.icon = TileIconGenerator.createPermissionIcon()
            } catch (e: Exception) {
                tile.icon = Icon.createWithResource(this, R.drawable.ic_tile_data_usage)
            }
            tile.updateTile()
            return
        }

        val formatted = ByteFormatter.formatBytes(todayBytes)
        val networkLabel = when (networkType) {
            ActiveNetworkType.WIFI -> "Wi-Fi"
            ActiveNetworkType.MOBILE -> "Mobile"
            ActiveNetworkType.OFFLINE -> "Offline"
        }

        tile.state = Tile.STATE_ACTIVE
        // Label includes network type so one-line launcher panels show both (e.g. "1.24 GB Mobile")
        tile.label = "$formatted $networkLabel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            tile.subtitle = "$networkLabel Today"
        }

        // Generate dynamic icon with active network indicator (cellular bars vs wifi arc)
        // and exact numbers so that HyperOS / MIUI renders it directly inside the circle
        try {
            tile.icon = TileIconGenerator.createUsageIcon(todayBytes, networkType)
        } catch (e: Exception) {
            tile.icon = Icon.createWithResource(this, R.drawable.ic_tile_data_usage)
        }

        tile.updateTile()
    }
}
