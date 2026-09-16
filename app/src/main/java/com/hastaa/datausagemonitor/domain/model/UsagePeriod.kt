package com.hastaa.datausagemonitor.domain.model

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters

/**
 * Represents the reporting time window for network usage.
 * Calculations are performed based on the device's local timezone.
 */
enum class UsagePeriod(val label: String) {
    TODAY("Today"),
    THIS_WEEK("This Week"),
    THIS_MONTH("This Month");

    /**
     * Calculates the start and end timestamp in milliseconds for this period.
     * @param nowMillis Current timestamp in epoch milliseconds (defaults to System.currentTimeMillis()).
     * @param zoneId Device's local ZoneId.
     * @return Pair of (startTimeMillis, endTimeMillis)
     */
    fun getTimeRange(
        nowMillis: Long = System.currentTimeMillis(),
        zoneId: ZoneId = ZoneId.systemDefault()
    ): Pair<Long, Long> {
        val today = LocalDate.now(zoneId)
        val startDateTime = when (this) {
            TODAY -> today.atStartOfDay(zoneId)
            THIS_WEEK -> today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).atStartOfDay(zoneId)
            THIS_MONTH -> today.withDayOfMonth(1).atStartOfDay(zoneId)
        }
        val startTimeMillis = startDateTime.toInstant().toEpochMilli()
        return Pair(startTimeMillis, nowMillis)
    }
}
