package com.hastaa.datausagemonitor.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.hastaa.datausagemonitor.MainActivity
import com.hastaa.datausagemonitor.R
import com.hastaa.datausagemonitor.data.repository.NetworkUsageRepository
import com.hastaa.datausagemonitor.domain.model.UsagePeriod
import com.hastaa.datausagemonitor.util.ByteFormatter
import com.hastaa.datausagemonitor.util.PermissionHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Material Design 3 Expressive Android Home Screen Widget Provider.
 * Displays today's network consumption, mobile/wifi distribution,
 * and allows quick refresh and launch.
 */
class DataUsageAppWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        updateAllWidgets(context, appWidgetManager, appWidgetIds)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_REFRESH_WIDGET) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val component = ComponentName(context, DataUsageAppWidgetProvider::class.java)
            val ids = appWidgetManager.getAppWidgetIds(component)
            if (ids.isNotEmpty()) {
                updateAllWidgets(context, appWidgetManager, ids)
            }
        }
    }

    private fun updateAllWidgets(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val hasPermission = PermissionHelper.hasUsageAccess(context)
                val repository = NetworkUsageRepository(context)
                val summary = if (hasPermission) {
                    repository.getDeviceSummary(UsagePeriod.TODAY)
                } else null

                for (widgetId in appWidgetIds) {
                    val views = RemoteViews(context.packageName, R.layout.widget_data_usage)

                    if (summary != null) {
                        val (valStr, unitStr) = ByteFormatter.formatBytesParts(summary.totalBytes)
                        views.setTextViewText(R.id.widget_text_value, valStr)
                        views.setTextViewText(R.id.widget_text_unit, unitStr)
                        views.setTextViewText(
                            R.id.widget_text_mobile,
                            "Mobile: ${ByteFormatter.formatBytes(summary.mobileBytes)}"
                        )
                        views.setTextViewText(
                            R.id.widget_text_wifi,
                            "Wi-Fi: ${ByteFormatter.formatBytes(summary.wifiBytes)}"
                        )
                        views.setTextViewText(R.id.widget_text_period, "Today")
                    } else {
                        views.setTextViewText(R.id.widget_text_value, "--")
                        views.setTextViewText(R.id.widget_text_unit, "")
                        views.setTextViewText(R.id.widget_text_mobile, "Access Required")
                        views.setTextViewText(R.id.widget_text_wifi, "Tap to setup")
                        views.setTextViewText(R.id.widget_text_period, "Setup Access")
                    }

                    // Click to launch app
                    val launchIntent = Intent(context, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    }
                    val launchPendingIntent = PendingIntent.getActivity(
                        context,
                        widgetId,
                        launchIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    views.setOnClickPendingIntent(R.id.widget_root, launchPendingIntent)

                    // Click to refresh widget
                    val refreshIntent = Intent(context, DataUsageAppWidgetProvider::class.java).apply {
                        action = ACTION_REFRESH_WIDGET
                    }
                    val refreshPendingIntent = PendingIntent.getBroadcast(
                        context,
                        widgetId + 10000,
                        refreshIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    views.setOnClickPendingIntent(R.id.widget_btn_refresh, refreshPendingIntent)

                    appWidgetManager.updateAppWidget(widgetId, views)
                }
            } catch (t: Throwable) {
                // Safeguard against background binder errors
            }
        }
    }

    companion object {
        const val ACTION_REFRESH_WIDGET = "com.hastaa.datausagemonitor.ACTION_REFRESH_WIDGET"

        fun notifyDataChanged(context: Context) {
            try {
                val intent = Intent(context, DataUsageAppWidgetProvider::class.java).apply {
                    action = ACTION_REFRESH_WIDGET
                }
                context.sendBroadcast(intent)
            } catch (t: Throwable) {
                // Safe ignore
            }
        }
    }
}
