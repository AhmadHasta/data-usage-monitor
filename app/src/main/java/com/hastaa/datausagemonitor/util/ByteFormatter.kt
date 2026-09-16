package com.hastaa.datausagemonitor.util

import java.util.Locale

/**
 * Formats raw byte counts into human-readable network usage strings.
 * Follows standard binary byte multipliers (1024 basis).
 */
object ByteFormatter {
    const val KB = 1024L
    const val MB = KB * 1024L
    const val GB = MB * 1024L
    const val TB = GB * 1024L

    fun formatBytes(bytes: Long): String {
        val (value, unit) = formatBytesParts(bytes)
        return "$value $unit"
    }

    fun formatBytesParts(bytes: Long): Pair<String, String> {
        if (bytes <= 0L) return Pair("0", "B")
        if (bytes < KB) return Pair(bytes.toString(), "B")

        return when {
            bytes < MB -> {
                val kb = bytes.toDouble() / KB
                if (kb >= 100) {
                    Pair(String.format(Locale.US, "%.0f", kb), "KB")
                } else {
                    Pair(String.format(Locale.US, "%.1f", kb).trimEnd('0').trimEnd('.'), "KB")
                }
            }
            bytes < GB -> {
                val mb = bytes.toDouble() / MB
                if (mb >= 100) {
                    Pair(String.format(Locale.US, "%.0f", mb), "MB")
                } else {
                    Pair(String.format(Locale.US, "%.2f", mb).trimEnd('0').trimEnd('.'), "MB")
                }
            }
            bytes < TB -> {
                val gb = bytes.toDouble() / GB
                Pair(String.format(Locale.US, "%.2f", gb), "GB")
            }
            else -> {
                val tb = bytes.toDouble() / TB
                Pair(String.format(Locale.US, "%.2f", tb), "TB")
            }
        }
    }
}
