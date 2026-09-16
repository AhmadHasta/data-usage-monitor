package com.hastaa.datausagemonitor.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.Icon

/**
 * Generates dynamic Bitmaps for Quick Settings Tile icons.
 * This ensures that on OEMs like Xiaomi (HyperOS / MIUI) where Control Center tiles
 * hide text labels and subtitles, the usage numbers (e.g. "1.24 GB") remain clearly
 * visible right inside the circular tile icon.
 */
object TileIconGenerator {

    private const val BITMAP_SIZE = 128

    fun createUsageIcon(bytes: Long): Icon {
        val (value, unit) = ByteFormatter.formatBytesParts(bytes)
        val bitmap = createUsageBitmap(value, unit)
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

        // Top line: SETUP
        paint.textSize = 38f
        canvas.drawText("SETUP", BITMAP_SIZE / 2f, 54f, paint)

        // Bottom line: ACCESS
        paint.textSize = 30f
        canvas.drawText("ACCESS", BITMAP_SIZE / 2f, 98f, paint)

        return Icon.createWithBitmap(bitmap)
    }

    private fun createUsageBitmap(value: String, unit: String): Bitmap {
        val bitmap = Bitmap.createBitmap(BITMAP_SIZE, BITMAP_SIZE, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Dynamic font sizing based on length of the number
        val valueTextSize = when {
            value.length <= 3 -> 50f // e.g. "428", "50", "0"
            value.length == 4 -> 42f // e.g. "1.24", "18.4"
            else -> 36f              // e.g. "102.5"
        }

        val unitTextSize = 30f // e.g. "MB", "GB", "KB"

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

        val centerX = BITMAP_SIZE / 2f

        // Top line: Number (e.g. 1.24)
        canvas.drawText(value, centerX, 54f, valuePaint)

        // Bottom line: Unit (e.g. GB)
        canvas.drawText(unit, centerX, 98f, unitPaint)

        return bitmap
    }
}
