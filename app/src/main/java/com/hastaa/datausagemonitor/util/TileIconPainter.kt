package com.hastaa.datausagemonitor.util

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF

object TileIconPainter {

    fun drawNetworkIcon(
        canvas: Canvas,
        networkType: ActiveNetworkType,
        centerX: Float,
        centerY: Float,
        scale: Float,
        strokeMultiplier: Float = 1.0f
    ) {
        when (networkType) {
            ActiveNetworkType.WIFI -> drawWifiIcon(canvas, centerX, centerY, scale, strokeMultiplier)
            ActiveNetworkType.MOBILE -> drawCellularBars(canvas, centerX, centerY, scale, strokeMultiplier)
            ActiveNetworkType.OFFLINE -> drawDataUsageIcon(canvas, centerX, centerY, scale, strokeMultiplier)
        }
    }

    fun drawCellularBars(
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

    fun drawWifiIcon(
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

    fun drawDataUsageIcon(
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
