package com.hastaa.datausagemonitor.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import com.hastaa.datausagemonitor.domain.model.TileConfig

object TileRingRenderer {

    fun createProgressRingBitmap(
        progressRatio: Float,
        config: TileConfig = TileConfig(),
        bitmapSize: Int = 128
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(bitmapSize, bitmapSize, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val centerX = bitmapSize / 2f
        val centerY = bitmapSize / 2f

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
        config: TileConfig = TileConfig(),
        bitmapSize: Int = 128
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(bitmapSize, bitmapSize, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val centerX = bitmapSize / 2f
        val centerY = bitmapSize / 2f

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
        TileIconPainter.drawNetworkIcon(canvas, networkType, centerX, centerY, iconScale, strokeMult)

        return bitmap
    }
}
