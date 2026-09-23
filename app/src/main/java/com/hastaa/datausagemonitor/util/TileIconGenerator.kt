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
        when (networkType) {
            ActiveNetworkType.WIFI -> drawWifiIcon(canvas, centerX, iconCenterY, scale = iconScale, strokeMultiplier = strokeMult)
            ActiveNetworkType.MOBILE -> drawCellularBars(canvas, centerX, iconCenterY, scale = iconScale, strokeMultiplier = strokeMult)
            ActiveNetworkType.OFFLINE -> drawDataUsageIcon(canvas, centerX, iconCenterY, scale = iconScale, strokeMultiplier = strokeMult)
        }

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
        val bitmap = Bitmap.createBitmap(BITMAP_SIZE, BITMAP_SIZE, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val centerX = BITMAP_SIZE / 2f
        val centerY = BITMAP_SIZE / 2f

        val strokeWidth = (config.ringStrokeWidthDp / 8f) * 14f
        val radius = if (config.ringDiameterDp <= 54) {
            26f + (config.ringDiameterDp - 36f) / 18f * (44f - 26f)
        } else {
            44f + (config.ringDiameterDp - 54f) / 46f * (58f - 44f)
        }

        val trackRect = RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius)
        val maxSweep = config.ringSweepAngleDeg.toFloat().coerceIn(180f, 360f)

        val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            style = Paint.Style.STROKE
            this.strokeWidth = strokeWidth
            strokeCap = Paint.Cap.ROUND
        }

        val trackPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            alpha = 75
            style = Paint.Style.STROKE
            this.strokeWidth = strokeWidth
            strokeCap = Paint.Cap.ROUND
        }

        if (maxSweep >= 355f) {
            val gapAngle = 22f
            val totalAvailable = 360f - 2 * gapAngle
            val ratio = progressRatio.coerceIn(0.08f, 0.92f)

            val sweep1 = totalAvailable * ratio
            val sweep2 = totalAvailable - sweep1

            val start1 = -90f + gapAngle / 2f
            val start2 = start1 + sweep1 + gapAngle

            canvas.drawArc(trackRect, start1, sweep1, false, progressPaint)
            canvas.drawArc(trackRect, start2, sweep2, false, trackPaint)
        } else {
            val bottomOpening = 360f - maxSweep
            val startAngle = 90f + bottomOpening / 2f
            val gapAngle = 14f
            val totalAvailable = (maxSweep - gapAngle).coerceAtLeast(10f)
            val ratio = progressRatio.coerceIn(0.06f, 0.94f)

            val sweep1 = totalAvailable * ratio
            val sweep2 = totalAvailable - sweep1

            val start1 = startAngle
            val start2 = start1 + sweep1 + gapAngle

            canvas.drawArc(trackRect, start1, sweep1, false, progressPaint)
            canvas.drawArc(trackRect, start2, sweep2, false, trackPaint)
        }

        return bitmap
    }

    fun createProgressWithIconBitmap(
        progressRatio: Float,
        networkType: ActiveNetworkType,
        config: TileConfig = TileConfig()
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(BITMAP_SIZE, BITMAP_SIZE, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val centerX = BITMAP_SIZE / 2f
        val centerY = BITMAP_SIZE / 2f

        val strokeWidth = (config.progressRingStrokeDp / 5f) * 8f
        val radius = if (config.progressRingDiameterDp <= 60) {
            26f + (config.progressRingDiameterDp - 40f) / 20f * (46f - 26f)
        } else {
            46f + (config.progressRingDiameterDp - 60f) / 40f * (60f - 46f)
        }
        val iconScale = if (config.progressIconSizeDp <= 24) {
            0.75f + (config.progressIconSizeDp - 14f) / 10f * (1.35f - 0.75f)
        } else {
            1.35f + (config.progressIconSizeDp - 24f) / 76f * (3.6f - 1.35f)
        }

        val trackRect = RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius)

        val gapAngle = 22f
        val totalAvailable = 360f - 2 * gapAngle
        val ratio = progressRatio.coerceIn(0.08f, 0.92f)

        val sweep1 = totalAvailable * ratio
        val sweep2 = totalAvailable - sweep1

        val start1 = -90f + gapAngle / 2f
        val start2 = start1 + sweep1 + gapAngle

        val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            style = Paint.Style.STROKE
            this.strokeWidth = strokeWidth
            strokeCap = Paint.Cap.ROUND
        }
        canvas.drawArc(trackRect, start1, sweep1, false, progressPaint)

        val trackPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            alpha = 70
            style = Paint.Style.STROKE
            this.strokeWidth = strokeWidth
            strokeCap = Paint.Cap.ROUND
        }
        canvas.drawArc(trackRect, start2, sweep2, false, trackPaint)

        val strokeMult = (config.iconStrokeWidthDp / 3f).coerceIn(0.4f, 2.5f)
        when (networkType) {
            ActiveNetworkType.WIFI -> drawWifiIcon(canvas, centerX, centerY, scale = iconScale, strokeMultiplier = strokeMult)
            ActiveNetworkType.MOBILE -> drawCellularBars(canvas, centerX, centerY, scale = iconScale, strokeMultiplier = strokeMult)
            ActiveNetworkType.OFFLINE -> drawDataUsageIcon(canvas, centerX, centerY, scale = iconScale, strokeMultiplier = strokeMult)
        }

        return bitmap
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

        when (networkType) {
            ActiveNetworkType.WIFI -> drawWifiIcon(canvas, centerX, centerY, scale = iconScale, strokeMultiplier = strokeMult)
            ActiveNetworkType.MOBILE -> drawCellularBars(canvas, centerX, centerY, scale = iconScale, strokeMultiplier = strokeMult)
            ActiveNetworkType.OFFLINE -> drawDataUsageIcon(canvas, centerX, centerY, scale = iconScale, strokeMultiplier = strokeMult)
        }

        return bitmap
    }

    private fun drawCellularBars(
        canvas: Canvas,
        centerX: Float,
        targetCenterY: Float,
        scale: Float,
        strokeMultiplier: Float = 1.0f
    ) {
        val maxBarHeight = 21f * scale
        val baselineY = targetCenterY + maxBarHeight / 2f

        val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            style = Paint.Style.FILL
        }

        val barWidth = 4.5f * scale * strokeMultiplier
        val spacing = 3.5f * scale
        val totalWidth = 4 * barWidth + 3 * spacing
        val startX = centerX - totalWidth / 2f

        val heights = floatArrayOf(6f * scale, 11f * scale, 16f * scale, 21f * scale)
        for (i in heights.indices) {
            val left = startX + i * (barWidth + spacing)
            val top = baselineY - heights[i]
            val rect = RectF(left, top, left + barWidth, baselineY)
            canvas.drawRoundRect(rect, barWidth / 2f, barWidth / 2f, fillPaint)
        }
    }

    private fun drawWifiIcon(
        canvas: Canvas,
        centerX: Float,
        targetCenterY: Float,
        scale: Float,
        strokeMultiplier: Float = 1.0f
    ) {
        val waveHeight = 17.5f * scale
        val dotRadius = (2.5f * scale * strokeMultiplier).coerceAtLeast(1.5f)
        val dotY = targetCenterY + (waveHeight - dotRadius) / 2f

        val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            style = Paint.Style.FILL
        }
        val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            style = Paint.Style.STROKE
            strokeWidth = 3.2f * scale * strokeMultiplier
            strokeCap = Paint.Cap.ROUND
        }

        canvas.drawCircle(centerX, dotY, dotRadius, dotPaint)

        val r1 = 9.5f * scale
        val rect1 = RectF(centerX - r1, dotY - r1, centerX + r1, dotY + r1)
        canvas.drawArc(rect1, 225f, 90f, false, strokePaint)

        val r2 = 17.5f * scale
        val rect2 = RectF(centerX - r2, dotY - r2, centerX + r2, dotY + r2)
        canvas.drawArc(rect2, 225f, 90f, false, strokePaint)
    }

    private fun drawDataUsageIcon(
        canvas: Canvas,
        centerX: Float,
        centerY: Float,
        scale: Float,
        strokeMultiplier: Float = 1.0f
    ) {
        val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            style = Paint.Style.STROKE
            strokeWidth = 3.5f * scale * strokeMultiplier
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
        }

        val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            style = Paint.Style.FILL
        }

        val dx = 7f * scale
        val arrowLen = 14f * scale
        val halfLen = arrowLen / 2f
        val headOffset = 4.5f * scale * strokeMultiplier

        canvas.drawLine(centerX - dx, centerY + halfLen, centerX - dx, centerY - halfLen, strokePaint)
        val upHead = Path().apply {
            moveTo(centerX - dx - headOffset, centerY - halfLen + 5f * scale)
            lineTo(centerX - dx, centerY - halfLen - 2f * scale)
            lineTo(centerX - dx + headOffset, centerY - halfLen + 5f * scale)
            close()
        }
        canvas.drawPath(upHead, fillPaint)

        canvas.drawLine(centerX + dx, centerY - halfLen, centerX + dx, centerY + halfLen, strokePaint)
        val downHead = Path().apply {
            moveTo(centerX + dx - headOffset, centerY + halfLen - 5f * scale)
            lineTo(centerX + dx, centerY + halfLen + 2f * scale)
            lineTo(centerX + dx + headOffset, centerY + halfLen - 5f * scale)
            close()
        }
        canvas.drawPath(downHead, fillPaint)
    }
}
