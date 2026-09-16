package com.hastaa.datausagemonitor.tile

import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.hastaa.datausagemonitor.MainActivity
import com.hastaa.datausagemonitor.data.repository.NetworkUsageRepository
import com.hastaa.datausagemonitor.domain.model.UsagePeriod
import com.hastaa.datausagemonitor.util.ByteFormatter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Quick Settings Tile Service that presents today's total network usage at a glance.
 * Utilizes Active Tile mode with cached fallback for instant rendering.
 */
class DataUsageTileService : TileService() {

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var refreshJob: Job? = null
    private val repository by lazy { NetworkUsageRepository(this) }

    override fun onStartListening() {
        super.onStartListening()
        // 1. Immediately display cached value to prevent lag in Quick Settings UI
        serviceScope.launch {
            val cached = repository.getCachedTodayUsage()
            applyTileState(cached.totalBytes)
        }

        // 2. Perform lightweight background device summary query to keep tile up-to-date
        refreshJob?.cancel()
        refreshJob = serviceScope.launch {
            val summary = repository.getDeviceSummary(UsagePeriod.TODAY)
            applyTileState(summary.totalBytes)
        }
    }

    override fun onStopListening() {
        refreshJob?.cancel()
        super.onStopListening()
    }

    override fun onClick() {
        super.onClick()
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
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

    private fun applyTileState(todayBytes: Long) {
        val tile = qsTile ?: return
        val formatted = ByteFormatter.formatBytes(todayBytes)

        tile.state = Tile.STATE_ACTIVE
        tile.label = "Data Usage"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            tile.subtitle = "$formatted Today"
        } else {
            // For API 26-28 where subtitle is not supported, label includes the number
            tile.label = "$formatted Today"
        }

        tile.updateTile()
    }
}
