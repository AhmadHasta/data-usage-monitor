package com.hastaa.datausagemonitor.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.drawable.Icon
import com.hastaa.datausagemonitor.data.local.TileConfig
import com.hastaa.datausagemonitor.data.local.TileContentStyle
import com.hastaa.datausagemonitor.data.local.TileIconChoice

/**
 * Generates dynamic Bitmaps for Quick Settings Tile icons following
 * Material Design 3 Expressive geometric principles.
 * Supports multiple content styles (Metric+Icon, Progress Ring, Progress+Icon, Icon Only).
 */
object TileIconGenerator {

    private const val BITMAP_SIZE = 128

    /**
     * Backward-compatible default usage icon.
     */
    fun createUsageIcon(bytes: Long, networkType: ActiveNetworkType): Icon {
        val (value, unit) = ByteFormatter.formatBytesParts(bytes)
        val bitmap = createUsageBitmap(value, unit, networkType)
        return Icon.createWithBitmap(bitmap)
    }

    /**
     * Generates icon based on the user's saved TileConfig.
     */
    fun createConfiguredIcon(
        bytes: Long,
        activeNetwork: ActiveNetworkType,
        config: TileConfig
    ): Icon {
        val (value, unit) = ByteFormatter.formatBytesParts(bytes)
        val quotaBytes = config.quotaLimitGigaBytes.toLong() * 1024L * 1024L * 1024L
        val progressRatio = if (quotaBytes > 0L) {
            (bytes.toFloat() / quotaBytes).coerceIn(0.08f, 1f)
        } else 0.5f

        val effectiveIconType = when (config.iconChoice) {
            TileIconChoice.AUTO -> activeNetwork
            TileIconChoice.WIFI -> ActiveNetworkType.WIFI
            TileIconChoice.CELLULAR -> ActiveNetworkType.MOBILE
            TileIconChoice.DATA_USAGE -> ActiveNetworkType.OFFLINE
        }

        val bitmap = when (config.contentStyle) {
            TileContentStyle.METRIC_WITH_ICON -> createUsageBitmap(value, unit, effectiveIconType, config)
            TileContentStyle.PROGRESS_RING -> createProgressRingBitmap(progressRatio, config)
            TileContentStyle.PROGRESS_WITH_ICON -> createProgressWithIconBitmap(progressRatio, effectiveIconType, config)
            TileContentStyle.ICON_ONLY -> createIconOnlyBitmap(effectiveIconType, config)
        }

        return Icon.createWithBitmap(bitmap)
    }

    fun createPermissionIcon(): Icon {
        val bitmap = Bitmap.createBitmap(BITMAP_SIZE, BITMAP_SIZE, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val centerX = BITMAP_SIZE / 2f

        val shieldPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            style = Paint.Style.STROKE
            strokeWidth = 3.5f
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
        }

        val shieldPath = Path().apply {
            moveTo(centerX, 16f)
            lineTo(centerX + 18f, 24f)
            quadTo(centerX + 18f, 42f, centerX, 50f)
            quadTo(centerX - 18f, 42f, centerX - 18f, 24f)
            close()
        }
        canvas.drawPath(shieldPath, shieldPaint)

        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            textSize = 28f
        }
        canvas.drawText("SETUP", centerX, 82f, textPaint)

        textPaint.textSize = 22f
        canvas.drawText("ACCESS", centerX, 110f, textPaint)

        return Icon.createWithBitmap(bitmap)
    }

    fun createUsageBitmap(
        value: String,
        unit: String,
        networkType: ActiveNetworkType,
        config: TileConfig = TileConfig()
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(BITMAP_SIZE, BITMAP_SIZE, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val centerX = BITMAP_SIZE / 2f

        val iconScale = (1.0f + (config.metricIconSizeDp - 16f) * 0.025f).coerceIn(0.6f, 3.2f)
        val iconCenterY = 18f

        val strokeMult = (config.iconStrokeWidthDp / 3f).coerceIn(0.4f, 2.5f)
        TileIconPainter.drawNetworkIcon(canvas, networkType, centerX, iconCenterY, scale = iconScale, strokeMultiplier = strokeMult)

        val baseValueSize = when {
            value.length <= 3 -> 44f
            value.length == 4 -> 38f
            else -> 32f
        }
        val valueTextSize = (config.metricValueTextSizeSp / 20f) * baseValueSize
        val unitTextSize = (config.metricUnitTextSizeSp / 11f) * 22f
        val spacing = config.metricSpacingDp.toFloat()

        val valuePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textAlign = Paint.Align.CENTER
            textSize = valueTextSize
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        }

        val unitPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textAlign = Paint.Align.CENTER
            textSize = unitTextSize
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        }

        val valueY = 70f + spacing * 1.5f
        val unitY = (valueY + unitTextSize + 4f + spacing * 1.5f).coerceAtMost(120f)

        canvas.drawText(value, centerX, valueY, valuePaint)
        canvas.drawText(unit, centerX, unitY, unitPaint)

        return bitmap
    }

    fun createProgressRingBitmap(
        progressRatio: Float,
        config: TileConfig = TileConfig()
    ): Bitmap {
        return TileRingRenderer.createProgressRingBitmap(progressRatio, config, BITMAP_SIZE)
    }

    fun createProgressWithIconBitmap(
        progressRatio: Float,
        networkType: ActiveNetworkType,
        config: TileConfig = TileConfig()
    ): Bitmap {
        return TileRingRenderer.createProgressWithIconBitmap(progressRatio, networkType, config, BITMAP_SIZE)
    }

    fun createIconOnlyBitmap(
        networkType: ActiveNetworkType,
        config: TileConfig = TileConfig()
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(BITMAP_SIZE, BITMAP_SIZE, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val centerX = BITMAP_SIZE / 2f
        val centerY = BITMAP_SIZE / 2f

        val iconScale = if (config.iconOnlySizeDp <= 38) {
            1.0f + (config.iconOnlySizeDp - 24f) / 14f * (2.0f - 1.0f)
        } else {
            2.0f + (config.iconOnlySizeDp - 38f) / 62f * (4.5f - 2.0f)
        }
        val strokeMult = (config.iconStrokeWidthDp / 3f).coerceIn(0.4f, 2.5f)

        TileIconPainter.drawNetworkIcon(canvas, networkType, centerX, centerY, scale = iconScale, strokeMultiplier = strokeMult)

        return bitmap
    }
}
