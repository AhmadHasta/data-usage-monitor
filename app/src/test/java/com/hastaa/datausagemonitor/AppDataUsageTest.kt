package com.hastaa.datausagemonitor

import com.hastaa.datausagemonitor.domain.model.AppDataUsage
import org.junit.Assert.assertEquals
import org.junit.Test

class AppDataUsageTest {

    @Test
    fun testTotalBytesCalculation() {
        val app = AppDataUsage(
            uid = 10001,
            packageName = "com.example.app",
            appName = "Test App",
            mobileBytes = 500L,
            wifiBytes = 1500L
        )
        assertEquals(2000L, app.totalBytes)
    }

    @Test
    fun testSortingByTotalUsage() {
        val app1 = AppDataUsage(uid = 1, packageName = "p1", appName = "A1", mobileBytes = 100L, wifiBytes = 200L) // 300L
        val app2 = AppDataUsage(uid = 2, packageName = "p2", appName = "A2", mobileBytes = 500L, wifiBytes = 500L) // 1000L
        val app3 = AppDataUsage(uid = 3, packageName = "p3", appName = "A3", mobileBytes = 50L, wifiBytes = 50L)   // 100L

        val list = listOf(app1, app2, app3)
        val sorted = list.sortedByDescending { it.totalBytes }

        assertEquals(2, sorted[0].uid)
        assertEquals(1, sorted[1].uid)
        assertEquals(3, sorted[2].uid)
    }
}
