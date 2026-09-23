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
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

import com.hastaa.datausagemonitor.data.local.TileConfig
import com.hastaa.datausagemonitor.data.local.TileIconChoice
import com.hastaa.datausagemonitor.data.local.TilePreferences
import com.hastaa.datausagemonitor.data.local.TileTextLayout

/**
 * Quick Settings Tile Service that dynamically presents today's network usage
 * based on the active connection (Mobile Data vs. Wi-Fi).
 * Automatically refreshes on panel expansion and periodically while visible.
 */
class DataUsageTileService : TileService() {

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var refreshJob: Job? = null
    private val repository by lazy { NetworkUsageRepository(this) }
    private val tilePreferences by lazy { TilePreferences(this) }

    override fun onStartListening() {
        super.onStartListening()
        try {
            val initialNetworkType = NetworkTypeHelper.getActiveNetworkType(this)

            // 1. Immediately display cached value for active network to prevent lag
            serviceScope.launch {
                try {
                    val config = tilePreferences.getTileConfig()
                    val cached = repository.getCachedTodayUsage()
                    val bytesToShow = when (initialNetworkType) {
                        ActiveNetworkType.WIFI -> cached.wifiBytes
                        ActiveNetworkType.MOBILE, ActiveNetworkType.OFFLINE -> cached.mobileBytes
                    }
                    applyTileState(bytesToShow, initialNetworkType, config)
                } catch (t: Throwable) {
                    // Safe fallback
                }
            }

            // 2. Refresh immediately and periodically every 5 seconds while panel is visible
            refreshJob?.cancel()
            refreshJob = serviceScope.launch {
                while (isActive) {
                    try {
                        val config = tilePreferences.getTileConfig()
                        val summary = repository.getDeviceSummary(config.period)
                        val currentNetwork = NetworkTypeHelper.getActiveNetworkType(this@DataUsageTileService)
                        val bytesToShow = when (currentNetwork) {
                            ActiveNetworkType.WIFI -> summary.wifiBytes
                            ActiveNetworkType.MOBILE, ActiveNetworkType.OFFLINE -> summary.mobileBytes
                        }
                        applyTileState(bytesToShow, currentNetwork, config)
                    } catch (t: Throwable) {
                        // Safe fallback
                    }
                    delay(5000L)
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

    private fun applyTileState(
        bytesToShow: Long,
        networkType: ActiveNetworkType,
        config: TileConfig = TileConfig()
    ) {
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

        val formatted = ByteFormatter.formatBytes(bytesToShow)
        val networkLabel = when (config.iconChoice) {
            TileIconChoice.WIFI -> "Wi-Fi"
            TileIconChoice.CELLULAR -> "Mobile"
            TileIconChoice.DATA_USAGE -> "Data"
            TileIconChoice.AUTO -> when (networkType) {
                ActiveNetworkType.WIFI -> "Wi-Fi"
                ActiveNetworkType.MOBILE -> "Mobile"
                ActiveNetworkType.OFFLINE -> "Offline"
            }
        }

        tile.state = Tile.STATE_ACTIVE

        when (config.textLayout) {
            TileTextLayout.SINGLE_LINE -> {
                tile.label = "$formatted $networkLabel"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    tile.subtitle = ""
                }
            }
            TileTextLayout.DUAL_LINE_NETWORK_FIRST -> {
                tile.label = "$networkLabel\n$formatted"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    tile.subtitle = formatted
                }
            }
            TileTextLayout.DUAL_LINE_METRIC_FIRST -> {
                tile.label = "$formatted\n$networkLabel"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    tile.subtitle = "$networkLabel ${config.period.label}"
                }
            }
        }

        try {
            tile.icon = TileIconGenerator.createConfiguredIcon(bytesToShow, networkType, config)
        } catch (e: Exception) {
            tile.icon = Icon.createWithResource(this, R.drawable.ic_tile_data_usage)
        }

        tile.updateTile()
    }
}
