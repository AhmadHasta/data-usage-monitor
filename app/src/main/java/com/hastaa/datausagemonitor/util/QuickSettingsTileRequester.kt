package com.hastaa.datausagemonitor.util

import android.app.StatusBarManager
import android.content.ComponentName
import android.content.Context
import android.graphics.drawable.Icon
import android.os.Build
import com.hastaa.datausagemonitor.R
import com.hastaa.datausagemonitor.tile.DataUsageTileService

object QuickSettingsTileRequester {

    fun requestAddTile(
        context: Context,
        onFeedback: (String) -> Unit
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            try {
                val statusBarManager = context.getSystemService(StatusBarManager::class.java)
                if (statusBarManager == null) {
                    onFeedback("Please add the tile via Control Center -> Edit")
                    return
                }
                val component = ComponentName(context, DataUsageTileService::class.java)
                statusBarManager.requestAddTileService(
                    component,
                    context.getString(R.string.tile_label),
                    Icon.createWithResource(context, R.drawable.ic_tile_data_usage),
                    context.mainExecutor
                ) { result ->
                    try {
                        when (result) {
                            StatusBarManager.TILE_ADD_REQUEST_RESULT_TILE_ALREADY_ADDED -> {
                                onFeedback("Data Usage tile is already in Quick Settings")
                            }
                            StatusBarManager.TILE_ADD_REQUEST_RESULT_TILE_ADDED -> {
                                onFeedback("Tile added successfully!")
                            }
                            else -> {
                                onFeedback("Open Quick Settings and tap Edit to add the tile")
                            }
                        }
                    } catch (_: Throwable) {
                    }
                }
            } catch (_: Throwable) {
                onFeedback("Tile is available in Quick Settings Edit menu")
            }
        } else {
            onFeedback("Swipe down Quick Settings and tap Edit to add the tile")
        }
    }
}
