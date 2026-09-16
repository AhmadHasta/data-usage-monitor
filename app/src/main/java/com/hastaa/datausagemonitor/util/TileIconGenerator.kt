package com.hastaa.datausagemonitor.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.drawable.Icon

/**
 * Generates dynamic Bitmaps for Quick Settings Tile icons.
 * Renders the active network indicator (Cellular bars or Wi-Fi wave) together with
 * the exact data usage count (e.g. "1.24 GB"), ensuring it is clearly visible inside
 * the circular toggle buttons of Xiaomi HyperOS / MIUI Control Center.
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

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        }

        paint.textSize = 38f
        canvas.drawText("SETUP", BITMAP_SIZE / 2f, 54f, paint)

        paint.textSize = 30f
        canvas.drawText("ACCESS", BITMAP_SIZE / 2f, 98f, paint)

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

        // 1. Draw top active network indicator (Cellular bars or Wi-Fi arc)
        when (networkType) {
            ActiveNetworkType.WIFI -> drawWifiIcon(canvas, centerX, 28f)
            ActiveNetworkType.MOBILE, ActiveNetworkType.OFFLINE -> drawCellularBars(canvas, centerX, 28f)
        }

        // 2. Value text sizing and placement
        val valueTextSize = when {
            value.length <= 3 -> 44f // e.g. "428", "50", "0"
            value.length == 4 -> 38f // e.g. "1.24", "18.4"
            else -> 32f              // e.g. "102.5"
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
            textSize = 26f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        }

        // 3. Draw Number & Unit
        canvas.drawText(value, centerX, 74f, valuePaint)
        canvas.drawText(unit, centerX, 110f, unitPaint)

        return bitmap
    }

    private fun drawCellularBars(canvas: Canvas, centerX: Float, baselineY: Float) {
        val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            style = Paint.Style.FILL
        }

        val barWidth = 4f
        val spacing = 3f
        val startX = centerX - 12.5f

        // 4 cellular signal bars of increasing height
        val heights = floatArrayOf(6f, 11f, 16f, 21f)
        for (i in heights.indices) {
            val left = startX + i * (barWidth + spacing)
            val top = baselineY - heights[i]
            val rect = RectF(left, top, left + barWidth, baselineY)
            canvas.drawRoundRect(rect, 1.5f, 1.5f, fillPaint)
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
            strokeWidth = 3f
            strokeCap = Paint.Cap.ROUND
        }

        // Center dot
        canvas.drawCircle(centerX, dotY, 2.5f, dotPaint)

        // Inner wave
        val r1 = 10f
        val rect1 = RectF(centerX - r1, dotY - r1, centerX + r1, dotY + r1)
        canvas.drawArc(rect1, 225f, 90f, false, strokePaint)

        // Outer wave
        val r2 = 18f
        val rect2 = RectF(centerX - r2, dotY - r2, centerX + r2, dotY + r2)
        canvas.drawArc(rect2, 225f, 90f, false, strokePaint)
    }
}
