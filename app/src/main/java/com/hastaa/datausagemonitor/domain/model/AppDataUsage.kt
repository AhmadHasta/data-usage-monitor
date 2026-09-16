package com.hastaa.datausagemonitor.domain.model

import android.graphics.drawable.Drawable

/**
 * Represents network usage attributed to a specific Android application / UID.
 */
data class AppDataUsage(
    val uid: Int,
    val packageName: String,
    val appName: String,
    val icon: Drawable? = null,
    val mobileBytes: Long = 0L,
    val wifiBytes: Long = 0L,
    val isSystemApp: Boolean = false
) {
    val totalBytes: Long
        get() = mobileBytes + wifiBytes
}
