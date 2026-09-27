package com.hastaa.datausagemonitor.tile

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.window.SplashScreen
import android.graphics.drawable.Icon
import com.hastaa.datausagemonitor.MainActivity
import com.hastaa.datausagemonitor.R
import com.hastaa.datausagemonitor.data.repository.NetworkUsageRepository
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
import kotlin.time.Duration.Companion.seconds

import com.hastaa.datausagemonitor.data.local.TilePreferences
import com.hastaa.datausagemonitor.domain.model.TileConfig
import com.hastaa.datausagemonitor.domain.model.TileIconChoice
import com.hastaa.datausagemonitor.domain.model.TileTextLayout

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
                } catch (e: Exception) {
                    if (e is kotlinx.coroutines.CancellationException) throw e
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
                    } catch (e: Exception) {
                        if (e is kotlinx.coroutines.CancellationException) throw e
                        // Safe fallback
                    }
                    delay(5.seconds)
                }
            }
        } catch (e: Exception) {
            // Guard against any unexpected system framework exceptions
        }
    }

    override fun onStopListening() {
        refreshJob?.cancel()
        super.onStopListening()
    }

    @Suppress("DEPRECATION", "StartActivityAndCollapseDeprecated")
    @SuppressLint("StartActivityAndCollapseDeprecated")
    override fun onClick() {
        super.onClick()
        try {
            val launchIntent = (packageManager.getLaunchIntentForPackage(packageName)
                ?: Intent(this, MainActivity::class.java)).apply {
                component = ComponentName(this@DataUsageTileService, MainActivity::class.java)
                action = Intent.ACTION_MAIN
                addCategory(Intent.CATEGORY_LAUNCHER)
                putExtra(MainActivity.EXTRA_FROM_QS_TILE, true)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                val options = ActivityOptions.makeBasic().apply {
                    splashScreenStyle = SplashScreen.SPLASH_SCREEN_STYLE_ICON
                }.toBundle()

                val pendingIntent = PendingIntent.getActivity(
                    this,
                    0,
                    launchIntent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
                    options
                )
                startActivityAndCollapse(pendingIntent)
            } else {
                @Suppress("DEPRECATION", "StartActivityAndCollapseDeprecated")
                startActivityAndCollapse(launchIntent)
            }
        } catch (e: Exception) {
            // Fallback for OEMs with non-standard TileService activity launcher behavior
            try {
                val fallbackIntent = Intent(this, MainActivity::class.java).apply {
                    action = Intent.ACTION_MAIN
                    addCategory(Intent.CATEGORY_LAUNCHER)
                    putExtra(MainActivity.EXTRA_FROM_QS_TILE, true)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                startActivity(fallbackIntent)
            } catch (ignored: Exception) {}
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
            TileTextLayout.METRIC_ONLY -> {
                tile.label = formatted
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    tile.subtitle = ""
                }
            }
            TileTextLayout.NETWORK_ONLY -> {
                tile.label = networkLabel
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    tile.subtitle = ""
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
