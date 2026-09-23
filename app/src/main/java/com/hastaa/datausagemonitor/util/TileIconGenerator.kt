package com.hastaa.datausagemonitor.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.drawable.Icon

/**
 * Generates dynamic Bitmaps for Quick Settings Tile icons following
 * Material Design 3 Expressive geometric principles.
 * Renders active network indicators (Cellular bars or Wi-Fi waves with rounded caps)
 * together with the data usage metric, optimized for HyperOS, MIUI, and standard Android QS panels.
 */
object TileIconGenerator {

    private const val BITMAP_SIZE = 128

    fun createUsageIcon(bytes: Long, networkType: ActiveNetworkType): Icon {
        val (value, unit) = ByteFormatter.formatBytesParts(bytes)
        val bitmap = createUsageBitmap(value, unit, networkType)
        return Icon.createWithBitmap(bitmap)
    }

    fun createPermissionIcon(): Icon {
        val bitmap = Bitmap.createBitmap(BITMAP_SIZE, BITMAP_SIZE, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val centerX = BITMAP_SIZE / 2f

        // Draw M3 Expressive Shield outline
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

        // Text labels with M3 bold hierarchy
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

    private fun createUsageBitmap(
        value: String,
        unit: String,
        networkType: ActiveNetworkType
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(BITMAP_SIZE, BITMAP_SIZE, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val centerX = BITMAP_SIZE / 2f

        // 1. Draw top active network indicator (Cellular pill bars or Wi-Fi round arcs)
        when (networkType) {
            ActiveNetworkType.WIFI -> drawWifiIcon(canvas, centerX, 26f)
            ActiveNetworkType.MOBILE, ActiveNetworkType.OFFLINE -> drawCellularBars(canvas, centerX, 26f)
        }

        // 2. Value text sizing with M3 Expressive weight contrast
        val valueTextSize = when {
            value.length <= 3 -> 46f // e.g. "428", "50", "0"
            value.length == 4 -> 40f // e.g. "1.24", "18.4"
            else -> 34f              // e.g. "102.5"
        }

        val valuePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textAlign = Paint.Align.CENTER
            textSize = valueTextSize
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        }

        val unitPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textAlign = Paint.Align.CENTER
            textSize = 24f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        }

        // 3. Draw Number & Unit with balanced baseline
        canvas.drawText(value, centerX, 74f, valuePaint)
        canvas.drawText(unit, centerX, 108f, unitPaint)

        return bitmap
    }

    private fun drawCellularBars(canvas: Canvas, centerX: Float, baselineY: Float) {
        val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            style = Paint.Style.FILL
        }

        val barWidth = 4.5f
        val spacing = 3.5f
        val startX = centerX - 14f

        // 4 cellular signal pill bars of increasing height
        val heights = floatArrayOf(6f, 11f, 16f, 21f)
        for (i in heights.indices) {
            val left = startX + i * (barWidth + spacing)
            val top = baselineY - heights[i]
            val rect = RectF(left, top, left + barWidth, baselineY)
            canvas.drawRoundRect(rect, barWidth / 2f, barWidth / 2f, fillPaint)
        }
    }

    private fun drawWifiIcon(canvas: Canvas, centerX: Float, dotY: Float) {
        val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            style = Paint.Style.FILL
        }
        val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            style = Paint.Style.STROKE
            strokeWidth = 3.2f
            strokeCap = Paint.Cap.ROUND
        }

        // Center dot
        canvas.drawCircle(centerX, dotY, 2.5f, dotPaint)

        // Inner wave
        val r1 = 9.5f
        val rect1 = RectF(centerX - r1, dotY - r1, centerX + r1, dotY + r1)
        canvas.drawArc(rect1, 225f, 90f, false, strokePaint)

        // Outer wave
        val r2 = 17.5f
        val rect2 = RectF(centerX - r2, dotY - r2, centerX + r2, dotY + r2)
        canvas.drawArc(rect2, 225f, 90f, false, strokePaint)
    }
}
