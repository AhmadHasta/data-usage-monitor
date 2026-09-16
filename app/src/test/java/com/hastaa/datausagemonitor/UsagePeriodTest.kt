package com.hastaa.datausagemonitor

import com.hastaa.datausagemonitor.domain.model.UsagePeriod
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class UsagePeriodTest {

    private val zoneId = ZoneId.of("Asia/Jakarta")

    @Test
    fun testTodayTimeRange() {
        val now = System.currentTimeMillis()
        val (start, end) = UsagePeriod.TODAY.getTimeRange(now, zoneId)

        assertEquals(now, end)
        assertTrue("Start must be before or equal to now", start <= end)

        // Verify start is midnight of today
        val expectedStart = LocalDate.now(zoneId).atStartOfDay(zoneId).toInstant().toEpochMilli()
        assertEquals(expectedStart, start)
    }

    @Test
    fun testThisWeekTimeRange() {
        val now = System.currentTimeMillis()
        val (start, end) = UsagePeriod.THIS_WEEK.getTimeRange(now, zoneId)

        assertEquals(now, end)
        assertTrue("Start must be before or equal to now", start <= end)

        val (todayStart, _) = UsagePeriod.TODAY.getTimeRange(now, zoneId)
        assertTrue("Week start must be before or equal to today start", start <= todayStart)
    }

    @Test
    fun testThisMonthTimeRange() {
        val now = System.currentTimeMillis()
        val (start, end) = UsagePeriod.THIS_MONTH.getTimeRange(now, zoneId)

        assertEquals(now, end)
        assertTrue("Start must be before or equal to now", start <= end)

        // Start must be day 1 of current month at 00:00:00
        val expectedMonthStart = LocalDate.now(zoneId).withDayOfMonth(1).atStartOfDay(zoneId).toInstant().toEpochMilli()
        assertEquals(expectedMonthStart, start)
    }
}
