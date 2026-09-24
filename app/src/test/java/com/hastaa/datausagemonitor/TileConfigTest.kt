package com.hastaa.datausagemonitor

import com.hastaa.datausagemonitor.domain.model.MetricDisplayMode
import com.hastaa.datausagemonitor.domain.model.TileConfig
import com.hastaa.datausagemonitor.domain.model.TileContentStyle
import org.junit.Assert.assertEquals
import org.junit.Test

class TileConfigTest {

    @Test
    fun testDefaultMetricDisplayMode() {
        val config = TileConfig()
        assertEquals(TileContentStyle.METRIC_WITH_ICON, config.contentStyle)
        assertEquals(MetricDisplayMode.NUMBERS_AND_ICON, config.metricDisplayMode)
    }

    @Test
    fun testMetricDisplayModeValues() {
        val modes = MetricDisplayMode.values()
        assertEquals(3, modes.size)
        assertEquals(MetricDisplayMode.NUMBERS_AND_ICON, modes[0])
        assertEquals(MetricDisplayMode.TEXT_ONLY, modes[1])
        assertEquals(MetricDisplayMode.ICON_ONLY, modes[2])
    }
}
