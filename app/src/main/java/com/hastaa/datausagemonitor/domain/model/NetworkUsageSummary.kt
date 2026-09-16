package com.hastaa.datausagemonitor.domain.model

/**
 * Summary of device-level network usage over a specific period.
 */
data class NetworkUsageSummary(
    val mobileBytes: Long = 0L,
    val wifiBytes: Long = 0L,
    val period: UsagePeriod = UsagePeriod.TODAY,
    val lastUpdatedMillis: Long = System.currentTimeMillis()
) {
    val totalBytes: Long
        get() = mobileBytes + wifiBytes
}
