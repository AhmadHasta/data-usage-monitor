package com.hastaa.datausagemonitor.data

import com.hastaa.datausagemonitor.domain.model.AppDataUsage

object DummyData {
    // 820 MB Mobile + 600 MB Wi-Fi = 1420 MB (~1.39 GiB / 1.42 GB metric)
    // To match exact display "1.42 GB" with ByteFormatter (binary GB):
    // 1.42 * 1024 * 1024 * 1024 = 1,524,713,062L
    // Mobile: 820 MB = 859,832,320L
    // Wi-Fi: 600 MB = 629,145,600L
    val mobileBytes: Long = 820L * 1024L * 1024L
    val wifiBytes: Long = 600L * 1024L * 1024L
    val totalBytes: Long = mobileBytes + wifiBytes

    val apps: List<AppDataUsage> = listOf(
        AppDataUsage(
            uid = 10001,
            packageName = "com.google.android.youtube",
            appName = "YouTube",
            icon = null,
            mobileBytes = 320L * 1024L * 1024L,
            wifiBytes = 100L * 1024L * 1024L
        ),
        AppDataUsage(
            uid = 10002,
            packageName = "com.instagram.android",
            appName = "Instagram",
            icon = null,
            mobileBytes = 200L * 1024L * 1024L,
            wifiBytes = 80L * 1024L * 1024L
        ),
        AppDataUsage(
            uid = 10003,
            packageName = "com.android.chrome",
            appName = "Chrome",
            icon = null,
            mobileBytes = 110L * 1024L * 1024L,
            wifiBytes = 80L * 1024L * 1024L
        ),
        AppDataUsage(
            uid = 10004,
            packageName = "org.telegram.messenger",
            appName = "Telegram",
            icon = null,
            mobileBytes = 70L * 1024L * 1024L,
            wifiBytes = 50L * 1024L * 1024L
        ),
        AppDataUsage(
            uid = 10005,
            packageName = "com.spotify.music",
            appName = "Spotify",
            icon = null,
            mobileBytes = 35L * 1024L * 1024L,
            wifiBytes = 60L * 1024L * 1024L
        ),
        AppDataUsage(
            uid = 10006,
            packageName = "com.discord",
            appName = "Discord",
            icon = null,
            mobileBytes = 25L * 1024L * 1024L,
            wifiBytes = 45L * 1024L * 1024L
        )
    )
}
