package com.hastaa.datausagemonitor.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.hastaa.datausagemonitor.domain.model.UsagePeriod
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.tileDataStore: DataStore<Preferences> by preferencesDataStore(name = "tile_preferences")

enum class TileContentStyle(val label: String, val subtitle: String) {
    METRIC_WITH_ICON(
        label = "Numbers & Icon",
        subtitle = "Usage metric, unit (GB/MB), and network icon above"
    ),
    PROGRESS_RING(
        label = "Progress Ring",
        subtitle = "Progress ring arc and quota gauge with dynamic sweep angle"
    ),
    PROGRESS_WITH_ICON(
        label = "Ring & Icon",
        subtitle = "Progress ring enclosing centered network icon"
    ),
    ICON_ONLY(
        label = "Icon Only",
        subtitle = "Minimalist network icon centered in tile"
    )
}

enum class TileIconChoice(val label: String, val subtitle: String) {
    AUTO(
        label = "Auto (Active Network)",
        subtitle = "Wi-Fi when connected to Wi-Fi, Cellular on mobile data"
    ),
    WIFI(
        label = "Always Wi-Fi",
        subtitle = "Always show Wi-Fi wave symbol"
    ),
    CELLULAR(
        label = "Always Cellular",
        subtitle = "Always show cellular signal bars"
    ),
    DATA_USAGE(
        label = "Data Usage",
        subtitle = "Show dual upload/download data arrows"
    )
}

enum class TileTextLayout(val label: String, val previewFirst: String, val previewSecond: String) {
    SINGLE_LINE(
        label = "Single Line (Value + Network)",
        previewFirst = "7.84 GB Wi-Fi",
        previewSecond = ""
    ),
    DUAL_LINE_NETWORK_FIRST(
        label = "Two Lines (Network on Top)",
        previewFirst = "WiFi",
        previewSecond = "7.84 GB"
    ),
    DUAL_LINE_METRIC_FIRST(
        label = "Two Lines (Value on Top)",
        previewFirst = "7.84 GB",
        previewSecond = "Wi-Fi Today"
    )
}

data class TileConfig(
    val contentStyle: TileContentStyle = TileContentStyle.METRIC_WITH_ICON,
    val iconChoice: TileIconChoice = TileIconChoice.AUTO,
    val textLayout: TileTextLayout = TileTextLayout.SINGLE_LINE,
    val period: UsagePeriod = UsagePeriod.TODAY,
    val quotaLimitGigaBytes: Int = 10,
    val metricIconSizeDp: Int = 16,
    val metricValueTextSizeSp: Int = 20,
    val metricUnitTextSizeSp: Int = 11,
    val metricSpacingDp: Int = 2,
    val ringDiameterDp: Int = 54,
    val ringStrokeWidthDp: Int = 8,
    val ringSweepAngleDeg: Int = 270,
    val progressIconSizeDp: Int = 24,
    val progressRingDiameterDp: Int = 60,
    val progressRingStrokeDp: Int = 5,
    val iconOnlySizeDp: Int = 38,
    val iconStrokeWidthDp: Int = 3
)

class TilePreferences(private val context: Context) {

    companion object {
        private val KEY_CONTENT_STYLE = stringPreferencesKey("tile_content_style")
        private val KEY_ICON_CHOICE = stringPreferencesKey("tile_icon_choice")
        private val KEY_TEXT_LAYOUT = stringPreferencesKey("tile_text_layout")
        private val KEY_PERIOD = stringPreferencesKey("tile_period")
        private val KEY_QUOTA_LIMIT_GB = longPreferencesKey("tile_quota_limit_gb")

        private val KEY_METRIC_ICON_SIZE = longPreferencesKey("tile_metric_icon_size")
        private val KEY_METRIC_VALUE_TEXT_SIZE = longPreferencesKey("tile_metric_value_text_size")
        private val KEY_METRIC_UNIT_TEXT_SIZE = longPreferencesKey("tile_metric_unit_text_size")
        private val KEY_METRIC_SPACING = longPreferencesKey("tile_metric_spacing")

        private val KEY_RING_DIAMETER = longPreferencesKey("tile_ring_diameter")
        private val KEY_RING_STROKE_WIDTH = longPreferencesKey("tile_ring_stroke_width")
        private val KEY_RING_SWEEP_ANGLE = longPreferencesKey("tile_ring_sweep_angle")

        private val KEY_PROGRESS_ICON_SIZE = longPreferencesKey("tile_progress_icon_size")
        private val KEY_PROGRESS_RING_DIAMETER = longPreferencesKey("tile_progress_ring_diameter")
        private val KEY_PROGRESS_RING_STROKE = longPreferencesKey("tile_progress_ring_stroke")

        private val KEY_ICON_ONLY_SIZE = longPreferencesKey("tile_icon_only_size")
        private val KEY_ICON_STROKE_WIDTH = longPreferencesKey("tile_icon_stroke_width")
    }

    val tileConfigFlow: Flow<TileConfig> = context.tileDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val styleStr = preferences[KEY_CONTENT_STYLE]
            val iconStr = preferences[KEY_ICON_CHOICE]
            val layoutStr = preferences[KEY_TEXT_LAYOUT]
            val periodStr = preferences[KEY_PERIOD]
            val limitGb = preferences[KEY_QUOTA_LIMIT_GB] ?: 10L

            TileConfig(
                contentStyle = runCatching { TileContentStyle.valueOf(styleStr ?: "") }
                    .getOrDefault(TileContentStyle.METRIC_WITH_ICON),
                iconChoice = runCatching { TileIconChoice.valueOf(iconStr ?: "") }
                    .getOrDefault(TileIconChoice.AUTO),
                textLayout = runCatching { TileTextLayout.valueOf(layoutStr ?: "") }
                    .getOrDefault(TileTextLayout.SINGLE_LINE),
                period = runCatching { UsagePeriod.valueOf(periodStr ?: "") }
                    .getOrDefault(UsagePeriod.TODAY),
                quotaLimitGigaBytes = limitGb.toInt().coerceIn(1, 200),

                metricIconSizeDp = (preferences[KEY_METRIC_ICON_SIZE] ?: 16L).toInt().coerceIn(12, 100),
                metricValueTextSizeSp = (preferences[KEY_METRIC_VALUE_TEXT_SIZE] ?: 20L).toInt().coerceIn(14, 30),
                metricUnitTextSizeSp = (preferences[KEY_METRIC_UNIT_TEXT_SIZE] ?: 11L).toInt().coerceIn(8, 18),
                metricSpacingDp = (preferences[KEY_METRIC_SPACING] ?: 2L).toInt().coerceIn(-2, 10),

                ringDiameterDp = (preferences[KEY_RING_DIAMETER] ?: 54L).toInt().coerceIn(36, 100),
                ringStrokeWidthDp = (preferences[KEY_RING_STROKE_WIDTH] ?: 8L).toInt().coerceIn(4, 14),
                ringSweepAngleDeg = (preferences[KEY_RING_SWEEP_ANGLE] ?: 270L).toInt().coerceIn(180, 360),

                progressIconSizeDp = (preferences[KEY_PROGRESS_ICON_SIZE] ?: 24L).toInt().coerceIn(14, 100),
                progressRingDiameterDp = (preferences[KEY_PROGRESS_RING_DIAMETER] ?: 60L).toInt().coerceIn(40, 100),
                progressRingStrokeDp = (preferences[KEY_PROGRESS_RING_STROKE] ?: 5L).toInt().coerceIn(2, 10),

                iconOnlySizeDp = (preferences[KEY_ICON_ONLY_SIZE] ?: 38L).toInt().coerceIn(24, 100),
                iconStrokeWidthDp = (preferences[KEY_ICON_STROKE_WIDTH] ?: 3L).toInt().coerceIn(1, 6)
            )
        }

    suspend fun getTileConfig(): TileConfig {
        return try {
            tileConfigFlow.first()
        } catch (e: Exception) {
            TileConfig()
        }
    }

    suspend fun updateConfig(config: TileConfig) {
        context.tileDataStore.edit { preferences ->
            preferences[KEY_CONTENT_STYLE] = config.contentStyle.name
            preferences[KEY_ICON_CHOICE] = config.iconChoice.name
            preferences[KEY_TEXT_LAYOUT] = config.textLayout.name
            preferences[KEY_PERIOD] = config.period.name
            preferences[KEY_QUOTA_LIMIT_GB] = config.quotaLimitGigaBytes.toLong()

            preferences[KEY_METRIC_ICON_SIZE] = config.metricIconSizeDp.toLong()
            preferences[KEY_METRIC_VALUE_TEXT_SIZE] = config.metricValueTextSizeSp.toLong()
            preferences[KEY_METRIC_UNIT_TEXT_SIZE] = config.metricUnitTextSizeSp.toLong()
            preferences[KEY_METRIC_SPACING] = config.metricSpacingDp.toLong()

            preferences[KEY_RING_DIAMETER] = config.ringDiameterDp.toLong()
            preferences[KEY_RING_STROKE_WIDTH] = config.ringStrokeWidthDp.toLong()
            preferences[KEY_RING_SWEEP_ANGLE] = config.ringSweepAngleDeg.toLong()

            preferences[KEY_PROGRESS_ICON_SIZE] = config.progressIconSizeDp.toLong()
            preferences[KEY_PROGRESS_RING_DIAMETER] = config.progressRingDiameterDp.toLong()
            preferences[KEY_PROGRESS_RING_STROKE] = config.progressRingStrokeDp.toLong()

            preferences[KEY_ICON_ONLY_SIZE] = config.iconOnlySizeDp.toLong()
            preferences[KEY_ICON_STROKE_WIDTH] = config.iconStrokeWidthDp.toLong()
        }
    }

    suspend fun setContentStyle(style: TileContentStyle) {
        context.tileDataStore.edit { it[KEY_CONTENT_STYLE] = style.name }
    }

    suspend fun setIconChoice(choice: TileIconChoice) {
        context.tileDataStore.edit { it[KEY_ICON_CHOICE] = choice.name }
    }

    suspend fun setTextLayout(layout: TileTextLayout) {
        context.tileDataStore.edit { it[KEY_TEXT_LAYOUT] = layout.name }
    }

    suspend fun setPeriod(period: UsagePeriod) {
        context.tileDataStore.edit { it[KEY_PERIOD] = period.name }
    }

    suspend fun setQuotaLimitGb(limitGb: Int) {
        context.tileDataStore.edit { it[KEY_QUOTA_LIMIT_GB] = limitGb.toLong() }
    }

    suspend fun setMetricIconSize(size: Int) {
        context.tileDataStore.edit { it[KEY_METRIC_ICON_SIZE] = size.toLong() }
    }

    suspend fun setMetricValueTextSize(size: Int) {
        context.tileDataStore.edit { it[KEY_METRIC_VALUE_TEXT_SIZE] = size.toLong() }
    }

    suspend fun setMetricUnitTextSize(size: Int) {
        context.tileDataStore.edit { it[KEY_METRIC_UNIT_TEXT_SIZE] = size.toLong() }
    }

    suspend fun setMetricSpacing(spacing: Int) {
        context.tileDataStore.edit { it[KEY_METRIC_SPACING] = spacing.toLong() }
    }

    suspend fun setRingDiameter(diameter: Int) {
        context.tileDataStore.edit { it[KEY_RING_DIAMETER] = diameter.toLong() }
    }

    suspend fun setRingStrokeWidth(width: Int) {
        context.tileDataStore.edit { it[KEY_RING_STROKE_WIDTH] = width.toLong() }
    }

    suspend fun setRingSweepAngle(angle: Int) {
        context.tileDataStore.edit { it[KEY_RING_SWEEP_ANGLE] = angle.toLong() }
    }

    suspend fun setProgressIconSize(size: Int) {
        context.tileDataStore.edit { it[KEY_PROGRESS_ICON_SIZE] = size.toLong() }
    }

    suspend fun setProgressRingDiameter(diameter: Int) {
        context.tileDataStore.edit { it[KEY_PROGRESS_RING_DIAMETER] = diameter.toLong() }
    }

    suspend fun setProgressRingStroke(stroke: Int) {
        context.tileDataStore.edit { it[KEY_PROGRESS_RING_STROKE] = stroke.toLong() }
    }

    suspend fun setIconOnlySize(size: Int) {
        context.tileDataStore.edit { it[KEY_ICON_ONLY_SIZE] = size.toLong() }
    }

    suspend fun setIconStrokeWidth(stroke: Int) {
        context.tileDataStore.edit { it[KEY_ICON_STROKE_WIDTH] = stroke.toLong() }
    }

    suspend fun resetStyleSizing(style: TileContentStyle) {
        context.tileDataStore.edit { prefs ->
            when (style) {
                TileContentStyle.METRIC_WITH_ICON -> {
                    prefs[KEY_METRIC_ICON_SIZE] = 16L
                    prefs[KEY_METRIC_VALUE_TEXT_SIZE] = 20L
                    prefs[KEY_METRIC_UNIT_TEXT_SIZE] = 11L
                    prefs[KEY_METRIC_SPACING] = 2L
                    prefs[KEY_ICON_STROKE_WIDTH] = 3L
                }
                TileContentStyle.PROGRESS_RING -> {
                    prefs[KEY_RING_DIAMETER] = 54L
                    prefs[KEY_RING_STROKE_WIDTH] = 8L
                    prefs[KEY_RING_SWEEP_ANGLE] = 270L
                }
                TileContentStyle.PROGRESS_WITH_ICON -> {
                    prefs[KEY_PROGRESS_ICON_SIZE] = 24L
                    prefs[KEY_PROGRESS_RING_DIAMETER] = 60L
                    prefs[KEY_PROGRESS_RING_STROKE] = 5L
                    prefs[KEY_ICON_STROKE_WIDTH] = 3L
                }
                TileContentStyle.ICON_ONLY -> {
                    prefs[KEY_ICON_ONLY_SIZE] = 38L
                    prefs[KEY_ICON_STROKE_WIDTH] = 3L
                }
            }
        }
    }
}
